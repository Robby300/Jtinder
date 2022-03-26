FROM maven:3.8.4-jdk-11-slim AS build
COPY src /home/client/src
COPY pom.xml /home/client/
USER root
RUN mvn -DskipTests=true -f /home/client/pom.xml clean package

FROM openjdk:11-ea-11-jdk-slim
COPY --from=build /home/client/target/client-0.0.1-SNAPSHOT.jar /usr/local/lib/client-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/client-0.0.1-SNAPSHOT.jar"]