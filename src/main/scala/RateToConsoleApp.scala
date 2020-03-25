import java.sql.Timestamp

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.{expr, lit}
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write

object RateToConsoleApp {

  val spark: SparkSession = SparkSession.builder.master("local[*]").getOrCreate()

  def streamDf(rowsPerSecond: Int = 1): DataFrame = {
    spark.readStream.format("rate").option("rowsPerSecond", rowsPerSecond).load()
  }

  def eventsDf(): DataFrame = {
    import spark.implicits._
    this.streamDf().map(x => {
      implicit val formats: DefaultFormats.type = DefaultFormats
      write(Event(x.getAs[Long]("value"), x.getAs[Timestamp]("timestamp")))
    }).toDF()
      .withColumn("topic", lit("events"))
      .withColumn("key", expr("uuid()"))
  }

  def load(): Unit = {
    val eventsDf = this.eventsDf()
    eventsDf.writeStream
      .format("console")
      .option("truncate", "false")
      .outputMode(OutputMode.Append())
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
