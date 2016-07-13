#!/bin/bash

JAR=analytics-data-service-1.0.0-SNAPSHOT.jar
CLASS=com.deleidos.analytics.data.service.websocket.util.StreamingClient
ENDPOINT=ws://localhost:8080/ws-stream
FILE="data/sample_rss.txt"
TOPIC=rss
DELAY=1000

nohup java -cp $JAR $CLASS $ENDPOINT $FILE $TOPIC $DELAY > /dev/null 2>&1 &
