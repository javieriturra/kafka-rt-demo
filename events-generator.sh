#!/usr/bin/env bash

set -e
SPARK_MASTER="spark://localhost:7077"
spark-submit  --master ${SPARK_MASTER} \
              --deploy-mode client \
              --class org.example.kafkartdemo.app.EventsGenerator \
              target/scala-2.11/kafka-rt-poc_2.11-0.1.jar
