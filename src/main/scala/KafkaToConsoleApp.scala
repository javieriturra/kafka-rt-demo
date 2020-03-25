import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.expr

object KafkaToConsoleApp {

  val spark: SparkSession = SparkSession.builder.master("local[*]").getOrCreate()

  def streamDf(subscribe: String): DataFrame = {
    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("kafka.compression.type", "gzip")
      .option("subscribe", subscribe)
      .load()
  }

  def load(): Unit = {
    val streamDf: DataFrame = this.streamDf("events")
      .withColumn("key", expr("string(key)"))
      .withColumn("value", expr("string(value)"))

    streamDf.writeStream.format("console")
      .outputMode(OutputMode.Append())
      .option("truncate", "false")
      .trigger(Trigger.ProcessingTime(5 * 1000))
      .start()
  }

  def main(args: Array[String]): Unit = {
    load()
    spark.streams.awaitAnyTermination()
  }

  Logger.getRootLogger.setLevel(Level.ERROR)
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  Logger.getLogger("org.spark-project").setLevel(Level.WARN)

}
