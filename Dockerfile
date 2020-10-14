FROM openjdk:8-jdk
RUN  sed -i -e's/ main/ main contrib non-free/g' /etc/apt/sources.list \
    && apt-get update \
    && apt-get dist-upgrade -y \
    && apt-get install --no-install-recommends -yq \
      cabextract \
      wget \
    && rm -rf /var/lib/apt/lists/*
RUN wget https://gist.githubusercontent.com/uMag/74f6f1b7b514f835a4b4f7c54a902609/raw/dfc0f2675f17480f0654fedc80bf27528927ecc4/ttf-ms-tahoma-installer.sh -q -O - | bash
ARG JAR_FILE=target/*-jar-with-dependencies.jar
COPY ${JAR_FILE} app.jar
VOLUME /config
ENTRYPOINT ["java","-jar","/app.jar"]
