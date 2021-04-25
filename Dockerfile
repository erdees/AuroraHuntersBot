#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build

COPY src /home/app/src
COPY pom.xml /home/app

RUN mvn -f /home/app/pom.xml clean install

#
# Package stage
#
FROM openjdk:8-jdk

RUN sed -i -e's/ main/ main contrib non-free/g' /etc/apt/sources.list \
    && apt-get update \
    && apt-get dist-upgrade -y \
    && apt-get install --no-install-recommends -yq \
      cabextract \
      wget \
      msttcorefonts \
      fonts-liberation

RUN wget https://gist.githubusercontent.com/uMag/74f6f1b7b514f835a4b4f7c54a902609/raw/dfc0f2675f17480f0654fedc80bf27528927ecc4/ttf-ms-tahoma-installer.sh -q -O - | bash

ARG JAR_FILE=target/*-jar-with-dependencies.jar
COPY --from=build /home/app/${JAR_FILE} app.jar

VOLUME /config
VOLUME /.cache

ENTRYPOINT ["java", "-jar", "/app.jar"]
