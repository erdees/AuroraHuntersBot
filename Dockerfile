FROM openjdk:8-jdk
RUN  sed -i -e's/ main/ main contrib non-free/g' /etc/apt/sources.list \
    && apt-get update \
    && apt-get dist-upgrade -y \
    && apt-get install --no-install-recommends -yq \
      msttcorefonts \
      fonts-liberation \
      cabextract \
      wget \
    && rm -rf /var/lib/apt/lists/*
RUN wget https://gist.githubusercontent.com/maxwelleite/913b6775e4e408daa904566eb375b090/raw/ttf-ms-tahoma-installer.sh -q -O - | bash
ARG JAR_FILE=target/*-jar-with-dependencies.jar
COPY ${JAR_FILE} app.jar
VOLUME /config
ENTRYPOINT ["java","-jar","/app.jar"]
