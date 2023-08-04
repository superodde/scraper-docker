FROM openjdk:18-alpine
MAINTAINER powrn.no
COPY target/scraper-0.0.1-SNAPSHOT.jar scraper-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/scraper-0.0.1-SNAPSHOT.jar"]