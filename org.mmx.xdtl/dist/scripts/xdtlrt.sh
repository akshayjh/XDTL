#!/bin/sh

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
LIB_DIR="$PRG_DIR"/xdtl

CLASSPATH="$LIB_DIR"/resources

for i in "$LIB_DIR"/*.jar; do
	CLASSPATH="$CLASSPATH":"$i" ;
done

export CLASSPATH
java -Xmx512M -Djava.security.egd=file:/dev/../dev/urandom org.mmx.xdtl.cli.Main $@
