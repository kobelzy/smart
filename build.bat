title smart build
%~d0
cd %~dp0
call mvn clean compile package -Dmaven.test.skip=true
echo ******************************smart build complete******************************
pause