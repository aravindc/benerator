#!/bin/sh
 
if [ -z "$BENERATOR_HOME" ]; then
  echo "Error: BENERATOR_HOME is not set. Please set the BENERATOR_HOME environment variable"
  echo "to the location of your benerator installation."
  exit 1
fi
. $BENERATOR_HOME/bin/benerator-wizard.sh
