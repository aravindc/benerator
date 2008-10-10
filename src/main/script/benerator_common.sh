#!/bin/sh

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
           fi
           ;;
esac

if [ -z "$BENERATOR_HOME" -o ! -d "$BENERATOR_HOME" ] ; then
  ## resolve links - $0 may be a link to benerator's home
  PRG="$0"
  progname=`basename "$0"`

  # need this for relative symlinks
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
    else
    PRG=`dirname "$PRG"`"/$link"
    fi
  done

  BENERATOR_HOME=`dirname "$PRG"`/..

  # make it fully qualified
  BENERATOR_HOME=`cd "$BENERATOR_HOME" && pwd`
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$BENERATOR_HOME" ] &&
    BENERATOR_HOME=`cygpath --unix "$BENERATOR_HOME"`
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
fi

# set BENERATOR_LIB location
BENERATOR_LIB="${BENERATOR_HOME}/lib"

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java 2> /dev/null `
    if [ -z "$JAVACMD" ] ; then
        JAVACMD=java
    fi
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

# set local classpath, don't overwrite the user's
LOCALCLASSPATH=.:$BENERATOR_HOME/bin/:$BENERATOR_HOME/lib/*

# if CLASSPATH_OVERRIDE env var is set, LOCALCLASSPATH will be
# user CLASSPATH first and benerator-found jars after.
# In that case, the user CLASSPATH will override benerator-found jars
#
# if CLASSPATH_OVERRIDE is not set, we'll have the normal behaviour
# with benerator-found jars first and user CLASSPATH after
if [ -n "$CLASSPATH" ] ; then
  # merge local and specified classpath 
  if [ -z "$LOCALCLASSPATH" ] ; then 
    LOCALCLASSPATH="$CLASSPATH"
  elif [ -n "$CLASSPATH_OVERRIDE" ] ; then
    LOCALCLASSPATH="$CLASSPATH:$LOCALCLASSPATH"
  else
    LOCALCLASSPATH="$LOCALCLASSPATH:$CLASSPATH"
  fi

  # remove class path from launcher -cp option
  CLASSPATH=""
fi

# For Cygwin, switch paths to appropriate format before running java
# For PATHs convert to unix format first, then to windows format to ensure
# both formats are supported. Probably this will fail on directories with ;
# in the name in the path. Let's assume that paths containing ; are more
# rare than windows style paths on cygwin.
if $cygwin; then
  if [ "$OS" = "Windows_NT" ] && cygpath -m .>/dev/null 2>/dev/null ; then
    format=mixed
  else
    format=windows
  fi
  BENERATOR_HOME=`cygpath --$format "$BENERATOR_HOME"`
  BENERATOR_LIB=`cygpath --$format "$BENERATOR_LIB"`
  if [ -n "$JAVA_HOME" ]; then
    JAVA_HOME=`cygpath --$format "$JAVA_HOME"`
  fi
  LOCALCLASSPATH=`cygpath --path --$format "$LOCALCLASSPATH"`
  if [ -n "$CLASSPATH" ] ; then
    CP_TEMP=`cygpath --path --unix "$CLASSPATH"`
    CLASSPATH=`cygpath --path --$format "$CP_TEMP"`
  fi
  CYGHOME=`cygpath --$format "$HOME"`
fi

# add a second backslash to variables terminated by a backslash under cygwin
if $cygwin; then
  case "$BENERATOR_HOME" in
    *\\ )
    BENERATOR_HOME="$BENERATOR_HOME\\"
    ;;
  esac
  case "$CYGHOME" in
    *\\ )
    CYGHOME="$CYGHOME\\"
    ;;
  esac
  case "$LOCALCLASSPATH" in
    *\\ )
    LOCALCLASSPATH="$LOCALCLASSPATH\\"
    ;;
  esac
  case "$CLASSPATH" in
    *\\ )
    CLASSPATH="$CLASSPATH\\"
    ;;
  esac
fi

# Readjust classpath for MKS
# expr match 
if [ \( "`expr $SHELL : '.*sh.exe$'`" -gt 0 \) -a \( "$cygwin" = "false" \) ]; then
  LOCALCLASSPATH=`echo $LOCALCLASSPATH | sed -E 's/([\d\w]*):([\d\w]*)/\1;\2/g
'`
fi
