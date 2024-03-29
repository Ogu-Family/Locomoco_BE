#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/locomoco
cd $REPOSITORY

APP_NAME=locomoco-server
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z "$CURRENT_PID" ]
then
  echo "> 종료할 애플리케이션이 없습니다."
else
  echo "> kill -9 $CURRENT_PID"
  kill -15 "$CURRENT_PID"
  sleep 5
fi

echo "> 실행권한 부여"
chmod +x $JAR_PATH

echo "> $JAR_PATH 배포"
nohup java -jar $JAR_PATH > server.log 2> run-error.log < /dev/null &
