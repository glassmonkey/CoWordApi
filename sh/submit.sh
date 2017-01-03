#!/bin/bash
PROJECT_HOME=/Users/shunsuke/work/spark
cd $PROJECT_HOME;
sbt clean package;
sudo $SPARK_HOME/bin/spark-submit \
--class com.example.SimpleApp \
--master local[4] \
$PROJECT_HOME/target/scala-2.11/simple-app_2.11-0.0.1.jar
