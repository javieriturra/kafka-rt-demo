import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{expr, from_json}
import org.apache.spark.sql.types.{LongType, StructField, StructType, TimestampType}

class KafkaToConsoleApp(processingTime: String) {

  val spark: SparkSession = SparkSession.builder.master("local[*]").getOrCreate()

  def writeMicroBatch: (DataFrame, Long) => Unit = {
    println("RateToConsoleApp.writeMicroBatch")
    (microBatchDf: DataFrame, _: Long) => microBatchDf.show(truncate = false)
  }

  def load(outputMode: OutputMode): Unit = {
    this.getEventsDf.writeStream
      .outputMode(outputMode)
      .trigger(Trigger.ProcessingTime(processingTime))
      .foreachBatch(this.writeMicroBatch)
      .start()
    spark.streams.awaitAnyTermination()
  }

  def getEventsDf: DataFrame = {
    val schema = StructType(List(
      StructField("value", LongType, nullable = true), StructField("timestamp", TimestampType, nullable = true)
    ))

    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("failOnDataLoss", "false")
      .option("subscribe", "events")
      .load()
      .withColumn("key", expr("string(key)"))
      .withColumn("value", from_json(expr("string(value)"), schema))
      .withColumn("eventValue", expr("value.value"))
      .withColumn("eventTimestamp", expr("value.timestamp"))
      .withWatermark(eventTime = "eventTimestamp", delayThreshold = "30 seconds")
      .drop("value")
  }

}

object KafkaToConsoleApp {

  def main(args: Array[String]): Unit = new KafkaToConsoleApp("10 seconds").load(OutputMode.Append())

}
