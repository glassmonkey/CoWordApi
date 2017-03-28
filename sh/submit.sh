#!/bin/bash
. environment.sh
cd $PROJECT_HOME;

echo $PROJECT_HOME/target/scala-${SCALA_VERSION}/${APP_NAME}-assembly-${APP_VERSION}.jar
echo ${NAME_SPACE}.${APP_NAME}

sbt clean
sbt assembly
#-Dapp_name=${APP_NAME},version=${APP_VERSION},scala_version=${SCALA_VERSION}
sudo $SPARK_HOME/bin/spark-submit \
--verbose \
--class ${NAME_SPACE}.${APP_NAME} \
--master local[*] \
--conf spark.executor.extraJavaOptions=-Dlog4j.configuration=log4j.properties \
$PROJECT_HOME/target/scala-${SCALA_VERSION}/${APP_NAME}-assembly-${APP_VERSION}.jar
#
