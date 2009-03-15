#!/bin/sh

if [ -z "$BENERATOR_HOME" ]; then
  echo "Error: BENERATOR_HOME is not set. Please set the BENERATOR_HOME environment variable"
  echo "to the location of your benerator installation."
  exit 1
fi
echo "Usage of benerator-gui.sh is deprecated. Pleas use maven-project-wizard.sh instead."
. $BENERATOR_HOME/bin/maven-project-wizard.sh
