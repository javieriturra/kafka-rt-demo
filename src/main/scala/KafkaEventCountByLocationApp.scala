import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{window, expr}
import org.apache.spark.sql.streaming.OutputMode

class KafkaEventCountByLocationApp(processingTime: String) extends KafkaToConsoleApp(processingTime) {

  private lazy val locationsDf = {
    spark.read.option("header", value = true).option("inferSchema", value = true).csv("./data/locations").cache()
  }

  override def writeMicroBatch: (DataFrame, Long) => Unit = {
    (microBatchDf: DataFrame, _: Long) => {
      microBatchDf.orderBy(microBatchDf.col("window.start"), microBatchDf.col("locationName")).show(truncate = false)
    }
  }

  override def getEventsDf: DataFrame = {
    val eventsDf = super.getEventsDf.withColumn("locationId", expr("eventValue % 3 + 1 "))
    val joinDf = eventsDf.join(locationsDf, eventsDf.col("locationId") === locationsDf.col("id"), "inner")
      .drop("id")
      .withColumnRenamed("name", "locationName")
    joinDf.groupBy(
      window(joinDf.col("eventTimestamp"), "60 seconds"), joinDf.col("locationName")
    ).count()
  }

}

object KafkaEventCountByLocationApp {

  def main(args: Array[String]): Unit = new KafkaEventCountByLocationApp("10 seconds").load(OutputMode.Update())

}
