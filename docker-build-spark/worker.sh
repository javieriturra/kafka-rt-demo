#!/usr/bin/env bash

SPARK_MASTER_URL="spark://spark-master:7077"
printenv
start-slave.sh ${SPARK_MASTER_URL} --cores 1 --memory 1G
tail -f /opt/spark/logs/*.out
