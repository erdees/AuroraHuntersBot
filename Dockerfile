FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/*-jar-with-dependencies.jar
COPY ${JAR_FILE} app.jar
VOLUME /config
EXPOSE 80
ENTRYPOINT ["java","-jar","/app.jar"]
