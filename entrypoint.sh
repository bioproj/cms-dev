#!/bin/sh
#USER_ID=$1
#GROUP_ID=$2
usermod -u $USER_ID shanjun
groupmod -g $GROUP_ID shanjun

#groupadd -g $GROUP_ID shanjun
#useradd -u $USER_ID -g $GROUP_ID -s /bin/bash -d /home/shanjun shanjun
chown -R shanjun:shanjun /home/shanjun

#su -l shanjun  -c "$@"
#"java -jar /opt/cms-boot-0.0.1-SNAPSHOT.jar"
# 执行其他启动命令
exec "$@"
