#!/usr/bin/env bash

set -e
SPARK_MASTER="spark://localhost:7077"
/opt/spark/bin/spark-submit --master ${SPARK_MASTER} \
  --deploy-mode client \
  --class RateToKafkaApp \
	--total-executor-cores 2 \
	--verbose \
  target/scala-2.11/kafka-rt-demo_2.11-0.1.jar
