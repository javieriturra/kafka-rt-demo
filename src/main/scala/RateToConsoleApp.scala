import org.apache.spark.sql.functions.{col, expr, struct, to_json}
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{DataFrame, SparkSession}

class RateToConsoleApp(processingTime: String) {

  val spark: SparkSession = SparkSession.builder.master("local[*]").getOrCreate()

  def load(outputMode: OutputMode): Unit = {
    getEventsDf.writeStream
      .outputMode(outputMode)
      .trigger(Trigger.ProcessingTime(this.processingTime))
      .foreachBatch(writeMicroBatch)
      .start()
    spark.streams.awaitAnyTermination()
  }

  def writeMicroBatch: (DataFrame, Long) => Unit = {
    println("RateToConsoleApp.writeMicroBatch")
    (microBatchDf: DataFrame, _: Long) =>
      microBatchDf.show(truncate = false)
  }

  val getEventsDf: DataFrame = {
    val rateDf: DataFrame = spark.readStream.format("rate").load()
    rateDf
      .withColumn("key", expr("uuid()"))
      .withColumn("value", to_json(struct(col("value"), col("timestamp"))))
      .select("key", "value")
  }

}

object RateToConsoleApp {

  def main(args: Array[String]): Unit = new RateToConsoleApp("5 seconds").load(OutputMode.Append())

}
