FROM eclipse-temurin:17-alpine AS BASE

# env
ARG TARGET_JAR="realworld-*.jar"
ENV TARGET_JAR=$TARGET_JAR

# copy build file
COPY ./build/libs/${TARGET_JAR} /

VOLUME /var/log

# set timezone to Asia/Seoul and character set to en_US.UTF-8
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
ENV TZ="Asia/Seoul"
ENV LANG="en_US.UTF-8"

# run java jar
ENTRYPOINT java \
-Duser.timezone=${TZ} \
-jar ${TARGET_JAR}
