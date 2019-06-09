FROM maven:3.6.0-jdk-11 AS maven

COPY ./src ./src

COPY ./pom.xml ./pom.xml

ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

RUN mvn package -Dmaven.test.skip=true

FROM openjdk:11-jre-slim

LABEL maintainer="publicdevop2019hw@gmail.com"

COPY --from=maven ./target/AuthService.jar ./AuthService.jar

EXPOSE 8080 8443

CMD java -jar AuthService.jar

