import org.apache.spark.sql.functions.{expr, struct, col, to_json}
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{DataFrame, SparkSession}

object RateToConsoleApp {

  val spark: SparkSession = SparkSession.builder.master("local[*]").getOrCreate()

  final val streamDf: DataFrame = spark.readStream.format("rate").option("rowsPerSecond", 1).load()

  val eventsDf: DataFrame = streamDf
    .withColumn("key", expr("uuid()"))
    .withColumn("value", to_json(struct(col("value"), col("timestamp"))))
    .select("key", "value")

  def load(): Unit = {
    eventsDf.writeStream // Only 1 stream is allowed
      .format("console")
      .option("truncate", "false")
      .outputMode(OutputMode.Update())
      .trigger(Trigger.ProcessingTime(5 * 1000))
      .start()
  }

  def main(args: Array[String]): Unit = {
    load()
    spark.streams.awaitAnyTermination()
  }

}
