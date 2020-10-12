FROM openjdk:8-jdk
RUN apt-get update \
    && apt-get dist-upgrade -y \
    && apt-get install --no-install-recommends -yq \
      xfonts-base \
      ghostscript \
    && rm -rf /var/lib/apt/lists/*
ARG JAR_FILE=target/*-jar-with-dependencies.jar
COPY ${JAR_FILE} app.jar
VOLUME /config
ENTRYPOINT ["java","-jar","/app.jar"]
