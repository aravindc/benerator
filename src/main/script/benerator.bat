@echo off
call classpath.bat
java -cp %CLASSPATH% org.databene.benerator.main.Benerator %*