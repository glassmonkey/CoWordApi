#!/bin/bash
. environment.sh
cd $PROJECT_HOME;

echo $PROJECT_HOME/target/scala-${SCALA_VERSION}/`echo $APP_NAME | tr "[:upper:]" "[:lower:]"`_${SCALA_VERSION}-${APP_VERSION}.jar
echo ${NAME_SPACE}.${APP_NAME}

sbt clean
sbt package
sudo $SPARK_HOME/bin/spark-submit \
--packages org.apache.spark:spark-streaming-kafka-0-8_2.11:2.1.0 \
--verbose \
--class ${NAME_SPACE}.${APP_NAME} \
--master local[*] \
--conf spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.properties \
$PROJECT_HOME/target/scala-${SCALA_VERSION}/`echo ${APP_NAME} | tr "[:upper:]" "[:lower:]"`_${SCALA_VERSION}-${APP_VERSION}.jar
#
