#!/bin/bash
. environment.sh
cd $PROJECT_HOME;
zk=`ps aux | grep zoo | grep -v grep | wc -l`
#count=`ps cax | grep mysqld | wc -l`
if [ $zk -ne 0 ]; then
    zkServer stop
fi
zkServer start
kafka=`ps aux | grep kafka | grep -v grep| wc -l`
if [ $kafka -ne 0 ]; then
    echo ${PROJECT_HOME}sh/server.properties
    kafka-server-stop
fi
kafka-server-start ${PROJECT_HOME}sh/server.properties &
