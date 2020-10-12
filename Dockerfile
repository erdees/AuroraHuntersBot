FROM openjdk:8-jdk
RUN  sed -i -e's/ main/ main contrib non-free/g' /etc/apt/sources.list \
    && apt-get update \
    && apt-get dist-upgrade -y \
    && apt-get install --no-install-recommends -yq \
      msttcorefonts \
      fonts-liberation \
    && rm -rf /var/lib/apt/lists/*
ARG JAR_FILE=target/*-jar-with-dependencies.jar
COPY ${JAR_FILE} app.jar
VOLUME /config
ENTRYPOINT ["java","-jar","/app.jar"]
