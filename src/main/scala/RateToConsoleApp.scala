import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.expr
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{DataFrame, SparkSession}

object RateToConsoleApp {

  val spark: SparkSession = SparkSession.builder.master("local[*]").getOrCreate()

  val streamDf: DataFrame = spark.readStream.format("rate").option("rowsPerSecond", 1).load()

  val eventsDf: DataFrame = streamDf.withColumn("key", expr("uuid()"))

  def load(): Unit = {

    eventsDf.writeStream
      .format("console")
      .option("truncate", "false")
      .outputMode(OutputMode.Update())
      .trigger(Trigger.ProcessingTime(5 * 1000))
      .start()

    eventsDf.writeStream
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
