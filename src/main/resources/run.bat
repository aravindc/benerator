call setup_external_libs.bat
set classpath=%classpath%;databene-benerator-0.3.jar;lib\commons-logging-1.0.4.jar;lib\databene-commons-0.2.jar;lib\databene-webdecs-0.2.jar
java -cp %classpath% org.databene.benerator.main.Benerator %*