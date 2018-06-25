@echo off
title smart

cd /d %~dp0
cd ..
set CLASSPATH=.;../lib/*;./;

set JAVA_DEBUG_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000

"%JAVA_HOME%\bin\java" %JAVA_DEBUG_OPTS% -Xms1024m -Xmx1024m -classpath %CLASSPATH% com.smart.web.SpringBoot

:EOF
pause	