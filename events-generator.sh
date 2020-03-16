#!/usr/bin/env bash

set -e
spark-submit  --master spark://localhost:7077 \
              --deploy-mode client \
              --class org.example.kafkartpoc.app.EventsGenerator \
              target/scala-2.11/kafka-rt-poc_2.11-0.1.jar
