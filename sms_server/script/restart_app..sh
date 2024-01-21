ps -ef |grep sms-server-0.0.1-SNAPSHOT.jar|awk '{print $2}'|xargs kill -9
nohup java -jar sms-server-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &