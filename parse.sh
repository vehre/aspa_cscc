#!/bin/bash
BASE_DIR=`dirname $0`
BASE_DIR=`realpath ${BASE_DIR}`
LIB_DIR="${BASE_DIR}/lib"
JAVA_CP="${LIB_DIR}/antlr.jar:${LIB_DIR}/log4j-1.2.8.jar:${LIB_DIR}/dom4j.jar:${LIB_DIR}/commons-collections.jar:${LIB_DIR}/jaxen.jar:${BASE_DIR}/build/classes"
echo "JAVA_CP='$JAVA_CP'"
java $JAVA_ARGS -cp $JAVA_CP gr.omadak.leviathan.asp.AspParser $@
