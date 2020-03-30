import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.window
import org.apache.spark.sql.streaming.OutputMode

class KafkaEventCountApp(processingTime: String) extends KafkaToConsoleApp(processingTime) {

  override def writeMicroBatch: (DataFrame, Long) => Unit = {
    (microBatchDf: DataFrame, _: Long) => {
      microBatchDf.orderBy(microBatchDf.col("window.start")).show(truncate = false)
    }
  }

  override def getEventsDf: DataFrame = {
    val eventsDf = super.getEventsDf
    eventsDf.groupBy(window(eventsDf.col("eventTimestamp"), "60 seconds")).count()
  }

}

object KafkaEventCountApp {

  def main(args: Array[String]): Unit = new KafkaEventCountApp("10 seconds").load(OutputMode.Complete())

}