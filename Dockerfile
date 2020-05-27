FROM openjdk:11-alpine
WORKDIR /opt/beebot
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
