#!/bin/bash
#JAVA_ARGS=-Xruntracer:enabled=true
#JAVA_ECP=$HOME/tmp/prof/tracer/lib/tracerapi.jar
#LD_LIBRARY_PATH=$HOME/tmp/prof/tracer/lib:${LD_LIBRARY_PATH}
#export LD_LIBRARY_PATH

JAVA_CP=build/classes:lib/antlr.jar:lib/log4j-1.2.7.jar:lib/dom4j.jar:lib/commons-collections.jar
#JAVA_CP=$JAVA_CP:$JAVA_ECP
java $JAVA_ARGS -cp $JAVA_CP Main2 $@
