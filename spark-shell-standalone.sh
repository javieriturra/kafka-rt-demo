#!/usr/bin/env bash

set -e
SPARK_MASTER="spark://localhost:7077"
/opt/spark/bin/spark-shell  --master ${SPARK_MASTER}
