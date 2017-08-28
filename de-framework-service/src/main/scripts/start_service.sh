#!/bin/bash

JAR=de-framework-service-0.0.1-SNAPSHOT.jar
LOG_CONFIG=config/log4j.properties

java -Xmx512m -Dlog4j.configuration=file:$LOG_CONFIG -jar $JAR 2>&1
