#!/bin/bash
BASE_DIR=$HOME/code/java/aspParser
LIB_DIR=$BASE_DIR/lib
JAVA_CP=$BASE_DIR/build/classes:$LIB_DIR/antlr.jar:$LIB_DIR/log4j-1.2.7.jar\
:$LIB_DIR/dom4j.jar:$LIB_DIR/commons-collections.jar
java $JAVA_ARGS -cp $JAVA_CP gr.omadak.leviathan.asp.AspParser $@
