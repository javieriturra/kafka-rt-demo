import java.sql.Timestamp

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.{expr, lit}
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write

object RateToKafkaApp {

  val spark: SparkSession = SparkSession.builder.getOrCreate()

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
      .outputMode(OutputMode.Append())
      .trigger(Trigger.ProcessingTime(5 * 1000))
      .foreachBatch((microBatchDf: DataFrame, _: Long) => {
        microBatchDf.show(10, truncate = false)
        microBatchDf.write
          .format("kafka") // Writing to Kafka in batch mode, since 2.4
          .option("checkpointLocation", "checkpoint")
          .option("kafka.bootstrap.servers", "kafka:9092")
          .option("kafka.compression.type", "gzip")
          .save()
      }).start()
  }

  def main(args: Array[String]): Unit = {
    load()
    spark.streams.awaitAnyTermination()
  }

}
