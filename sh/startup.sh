#!/usr/bash

JAVA_OPTS="-server -Xms1024m -Xmx1024m -XX:MaxMetaspaceSize=100m -XX:+PrintGCDetails -Xloggc:/root/jvm/gc.log -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError"
PID="PID"

if [ -s ${PID} ]; then
    pid=`cat ${PID}`
    if [ `jps | grep ${pid} | wc -l` -eq 1 ]; then
        echo "push server is running"
        exit 0
    fi
fi

nohup java -jar push-server.jar ${JAVA_OPTS} 2>&1 > push-server.log &

if [ $? -eq 0 ]; then
    #echo "wait..."
    jps -lvm | grep push-server.jar | awk '{print $1}' > ${PID}
else
    echo "push server start failed"
fi