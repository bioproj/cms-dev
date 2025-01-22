
#依赖的父镜像
FROM  registry.cn-wulanchabu.aliyuncs.com/minebiomeapp/java:8

WORKDIR /opt
RUN groupadd -g 1001 shanjun
RUN useradd -m shanjun -u 1001 -g 1001  -s /bin/sh

COPY cms-boot/target/cms-boot-0.0.1-SNAPSHOT.jar  cms-boot-0.0.1-SNAPSHOT.jar

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
USER shanjun
# ENTRYPOINT ["/entrypoint.sh"]
CMD ["java","-jar","cms-boot-0.0.1-SNAPSHOT.jar"]
# ,"java -jar /opt/cms-boot-0.0.1-SNAPSHOT.jar"