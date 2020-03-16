#!/usr/bin/env bash

printenv
start-master.sh
tail -f /opt/spark/logs/*.out
