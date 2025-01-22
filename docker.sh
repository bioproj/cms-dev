#/bin/bash

git pull

mvn clean
mvn install  -DskipTests=true

docker build -t master:5000/spring-single/cms-boot.jar .
#docker push master:5000/spring-single/cms-boot.jar

docker tag master:5000/spring-single/cms-boot.jar  registry.cn-wulanchabu.aliyuncs.com/minebiomeapp/cms-boot.jar

docker push  registry.cn-wulanchabu.aliyuncs.com/minebiomeapp/cms-boot.jar

docker stop cms-boot

docker run --rm \
    --name cms-boot \
    -v /home/shanjun/cms:/home/shanjun/cms \
    -p 8081:8080 \
    master:5000/spring-single/cms-boot.jar \
    java -jar cms-boot-0.0.1-SNAPSHOT.jar



 docker logs -f  mbiolance-single-platform

 docker run --rm -it -e USER_ID=1000 -e GROUP_ID=1000 -p 8089:8082  \
  -v /home/wangyang/cms:/root/cms \
  master:5000/spring-single/cms-boot.jar  \
  java -jar /opt/cms-boot-0.0.1-SNAPSHOT.jar

 docker run --rm -it -e USER_ID=1000 -e GROUP_ID=1000 -p 8089:8082  \
  -v /home/wangyang/cms:/home/shanjun/cms  \
  master:5000/spring-single/cms-boot.jar  \
  java -jar /opt/cms-boot-0.0.1-SNAPSHOT.jar

 docker run --rm -it -e USER_ID=1001 -e GROUP_ID=1001 -p 8089:8082  \
  -v /home/wangyang/cms:/home/shanjun/cms \
  master:5000/spring-single/cms-boot.jar  \
  su  -l  shanjun   -c 'java -jar /opt/cms-boot-0.0.1-SNAPSHOT.jar'
