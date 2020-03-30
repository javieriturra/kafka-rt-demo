import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{DataFrame, SparkSession}


class RateToKafkaApp(processingTime: String) extends RateToConsoleApp(processingTime) {

  override def writeMicroBatch: (DataFrame, Long) => Unit = {
    println("RateToKafkaApp.writeMicroBatch")
    (microBatchDf: DataFrame, _: Long) =>
      microBatchDf.show(truncate = false)
      microBatchDf.write
        .format("kafka")
        .option("kafka.bootstrap.servers", "localhost:9092")
        .option("topic", "events")
        .save()
  }

}

object RateToKafkaApp {

  def main(args: Array[String]): Unit = new RateToKafkaApp("5 seconds").load(OutputMode.Append())

}
