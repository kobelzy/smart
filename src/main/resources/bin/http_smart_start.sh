#!/bin/bash

cd ..
libs=.
for file in `ls ../lib/`
    do
        libs=$libs:../lib/$file
    done
echo $libs
declare -x CATALINA_OPTS="-server -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
java -cp $libs:./webapp/WEB-INF/classes $CATALINA_OPTS -Xms1024m -Xmx1024m -Drelease=true com.smart.web.SpringBoot
