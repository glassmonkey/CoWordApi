#!/bin/bash
. environment.sh
cd $PROJECT_HOME;
python ${PROJECT_HOME}src/publish/twitter/main.py print
