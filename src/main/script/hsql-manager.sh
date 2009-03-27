#!/bin/sh

if [ -z "$BENERATOR_HOME" ]; then
  echo "Error: BENERATOR_HOME is not set. Please set the BENERATOR_HOME environment variable"
  echo "to the location of your benerator installation."
  exit 1
fi
. $BENERATOR_HOME/bin/benerator_common.sh

java -cp "$BENERATOR_HOME/lib/*" org.hsqldb.util.DatabaseManager --driver org.hsqldb.jdbcDriver --url jdbc:hsqldb:hsql://localhost:9001 --user sa --password ""