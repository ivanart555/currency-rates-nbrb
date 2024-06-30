FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/currency-rates-nbrb-1.0-SNAPSHOT.jar app.jar

EXPOSE map[8080/tcp:{}]

ENTRYPOINT ["java", "-jar", "app.jar"]