#!/bin/bash
. environment.sh
cd $PROJECT_HOME;
sbt clean package;
sudo $SPARK_HOME/bin/spark-submit \
--class com.example.SimpleApp \
--master local[4] \
$PROJECT_HOME/target/scala-${SCALA_VERSION}/simple-app_${SCALA_VERSION}-${APP_VERSION}.jar
