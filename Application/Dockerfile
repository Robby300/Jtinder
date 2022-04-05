FROM maven:3.8.4-jdk-11-slim AS build
COPY src /home/jtinder/src
COPY pom.xml /home/jtinder/
USER root
RUN mvn -DskipTests=true -f /home/jtinder/pom.xml clean package

FROM openjdk:11-ea-11-jdk-slim
COPY --from=build /home/jtinder/target/jtinder-0.0.1-SNAPSHOT.jar /usr/local/lib/jtinder-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/jtinder-0.0.1-SNAPSHOT.jar"]
