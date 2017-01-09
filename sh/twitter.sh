#!/bin/bash
. environment.sh
cd $PROJECT_HOME;
python ${PROJECT_HOME}/publish/twitter/main.py print
