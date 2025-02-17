FROM openjdk:21-jdk
MAINTAINER powrn.no
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]