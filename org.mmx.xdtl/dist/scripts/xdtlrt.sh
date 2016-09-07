#!/bin/bash

if [[ $@ == *"--help"* ]]
then
  echo "XDTL RT Environment variables";
  echo "XDTL_LOG_LEVEL = INFO | DEBUG | ERROR, default INFO";
  echo "XDTL_LOG_APPENDER = CONSOLE | FILE, default CONSOLE";
  echo "XDTL_LOG_FILE = path to created log file in FILE appender, default xdtl.log in current folder";
  echo "XDTL_JAVA_OPTIONS = user set other java options and system variables";
  exit;
fi

PRG="$0"

# resolve links
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRG_DIR=`dirname "$PRG"`
LIB_DIR="$PRG_DIR"/lib
export XDTL_RTM_DIR=`cd "$PRG_DIR"; pwd`

CLASSPATH="$PRG_DIR"/resources

for i in "$LIB_DIR"/*.jar; do
	CLASSPATH="$CLASSPATH":"$i" ;
done

XDTL_OPTS=
if [ "$XDTL_HOME" ]; then
	XDTL_OPTS="-home=$XDTL_HOME"
fi

# if run with debug switch -d, set log level to DEBUG, appender to console
if [[ $@ == *"-d "* ]]; then
  XDTL_LOG_LEVEL="DEBUG"
  XDTL_LOG_APPENDER="CONSOLE"
fi

# default loglevel and appender
if [ -z "$XDTL_LOG_LEVEL" ]; then
	XDTL_LOG_LEVEL="INFO"
fi
if [ -z "$XDTL_LOG_APPENDER" ]; then
	XDTL_LOG_APPENDER="CONSOLE"
fi

if [ $XDTL_LOG_APPENDER = "FILE" ]; then
	if [ -z "$XDTL_LOG_FILE" ]; then
		XDTL_LOG_FILE="xdtl.log"
	fi
fi

if [[ ! $XDTL_JAVA_OPTIONS =~ -Xmx[0-9]+M ]]; then
  XDTL_JAVA_OPTIONS="-Xmx2048M $XDTL_JAVA_OPTIONS"
fi

XDTL_JAVA_OPTIONS="-DxdtlRootLogger=$XDTL_LOG_LEVEL,$XDTL_LOG_APPENDER $XDTL_JAVA_OPTIONS"

if [ "$XDTL_LOG_FILE" ]; then
	XDTL_JAVA_OPTIONS="-DxdtlLogFile=$XDTL_LOG_FILE $XDTL_JAVA_OPTIONS"
fi

export CLASSPATH
java $XDTL_JAVA_OPTIONS -Djava.security.egd=file:/dev/../dev/urandom org.mmx.xdtl.cli.Main $XDTL_OPTS $@
