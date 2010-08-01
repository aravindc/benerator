#!/bin/sh

if [ -z "$BENERATOR_HOME" ]; then
  echo "Error: BENERATOR_HOME is not set. Please set the BENERATOR_HOME environment variable"
  echo "to the location of your benerator installation."
  exit 1
fi
. $BENERATOR_HOME/bin/benerator_common.sh
benerator_exec_command="exec \"$JAVACMD\" $BENERATOR_OPTS -classpath \"$LOCALCLASSPATH\" org.databene.benerator.main.NewProjectWizard $*"
eval $benerator_exec_command
