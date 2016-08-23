#!/bin/bash

JAR=analytics-data-service-1.0.0-SNAPSHOT.jar
CONFIG=config/data-service-config.json
LOG_CONFIG=config/log4j.properties

export HADOOP_USER_NAME=hdfs

java -Dlog4j.configuration=file:$LOG_CONFIG -jar $JAR $CONFIG 2>&1
