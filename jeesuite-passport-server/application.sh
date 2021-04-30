#!/bin/bash
#
# chkconfig:   - 20 80
# description: Starts and stops the App.
# author:vakinge

ENV=dev
RUNNING_USER=vakinge
ADATE=`date +%Y%m%d%H%M%S`
APP_NAME=jeesuite-passport-server

APP_HOME=`pwd`
dirname $0|grep "^/" >/dev/null
if [ $? -eq 0 ];then
   APP_HOME=`dirname $0`
else
    dirname $0|grep "^\." >/dev/null
    retval=$?
    if [ $retval -eq 0 ];then
        APP_HOME=`dirname $0|sed "s#^.#$APP_HOME#"`
    else
        APP_HOME=`dirname $0|sed "s#^#$APP_HOME/#"`
    fi
fi

APP_HOME=$APP_HOME/target

JAR_FILE=$APP_HOME/$APP_NAME.jar

LOG_FILE=$APP_HOME/logs/$APP_NAME.out
#JVM参数
JVM_OPTS="-Dname=$APP_NAME -Djeesuite.configcenter.profile=$ENV -Duser.timezone=Asia/Shanghai -Xms256M -Xmx512M"

pid=0

build(){
  checkpid
  if [ ! -n "$pid" ]; then
    mvn clean package -DskipTests=true
  else
    echo "$APP_NAME is runing PID: $pid,请先停止应用"   
  fi
}

start(){
  if [ ! -f "$JAR_FILE" ];then
    build
  fi
  checkpid
  if [ ! -n "$pid" ]; then
    echo "应用路径:$JAR_FILE"
    if [ ! -d "$APP_HOME/logs" ];then
       mkdir $APP_HOME/logs
    fi
    nohup java -jar $JVM_OPTS $JAR_FILE > $LOG_FILE 2>&1 &
    echo "---------------------------------"
    echo "启动完成，按CTRL+C退出日志界面即可>>>>>"
    echo "---------------------------------"
    sleep 2s
    tail -f $LOG_FILE
  else
      echo "$APP_NAME is runing PID: $pid"   
  fi

}


status(){
   checkpid
   if [ ! -n "$pid" ]; then
     echo "$APP_NAME not runing"
   else
     echo "$APP_NAME runing PID: $pid"
   fi 
}

checkpid(){
    pid=`ps -ef |grep $APP_NAME |grep -v grep |awk '{print $2}'`
}

stop(){
    checkpid
    if [ ! -n "$pid" ]; then
     echo "$APP_NAME not runing"
    else
      echo "$APP_NAME stop..."
      kill -9 $pid
    fi 
}

restart(){
    stop 
    sleep 1s
    rm -rf $APP_HOME/*
    start
}

case $1 in  
          build) build;;
          start) start;;  
          stop)  stop;; 
          restart)  restart;;  
          status)  status;;   
              *)  echo "require build|start|stop|restart|status"  ;;  
esac 