#!/bin/bash
. environment.sh
cd $PROJECT_HOME;
count=`ps -ef | grep node | grep $PROJECT_HOME | grep -v grep | wc -l`
#count=`ps cax | grep mysqld | wc -l`
if [ $count -ne 0 ]; then
    npm stop;
fi
npm start
