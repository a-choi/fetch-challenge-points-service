FROM maven:3.8.2-jdk-11-slim AS build
WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn -B -e -C de.qaware.maven:go-offline-maven-plugin:resolve-dependencies
COPY src src
RUN mvn -B -e -o package

FROM openjdk
COPY --from=build /usr/src/app/target/*.jar ./

EXPOSE 8080
ENTRYPOINT ["java","-jar","fetch-challenge-points-service-1.0.0.jar"]