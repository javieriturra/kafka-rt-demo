import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.expr

object KafkaToConsoleApp {

  val spark: SparkSession = SparkSession.builder.getOrCreate()

  def streamDf(subscribe: String): DataFrame = {
    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "kafka:9092")
      .option("kafka.compression.type", "gzip")
      .option("kafka.reconnect.backoff.max.ms", 10 * 1000)
      .option("kafka.reconnect.backoff.ms", 5 * 1000)
      .option("failOnDataLoss", "false")
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
      .trigger(Trigger.ProcessingTime(10 * 1000))
      .start()
  }

  def main(args: Array[String]): Unit = {
    load()
    spark.streams.awaitAnyTermination()
  }

}
