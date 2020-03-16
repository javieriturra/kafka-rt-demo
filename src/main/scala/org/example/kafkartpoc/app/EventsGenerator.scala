package org.example.kafkartpoc.app

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.StreamingQueryListener.{QueryProgressEvent, QueryStartedEvent, QueryTerminatedEvent}
import org.apache.spark.sql.streaming.{OutputMode, StreamingQueryListener, Trigger}

object EventsGenerator {

  def load(): Unit = {
    val spark: SparkSession = SparkSession.builder.getOrCreate()

    val rateDf = spark.readStream
      .format("rate")
      .option("rowsPerSecond", 10)
      .load()
      .selectExpr(
        "timestamp AS timestamp",
        "value % 7 + 1 AS place_id",
        "round(rand() * 10000, 0) AS total"
      )

    rateDf.writeStream
      .format("console")
      .outputMode(OutputMode.Append())
      .option("truncate", "false")
      .option("numRows", 10)
      .trigger(Trigger.ProcessingTime(5 * 1000))
      .start()
    spark.streams.awaitAnyTermination()
  }

  def main(args: Array[String]): Unit = {
    load()
  }

}
