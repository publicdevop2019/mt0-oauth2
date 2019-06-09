FROM openjdk:11-jre-slim

LABEL maintainer="publicdevop2019hw@gmail.com"

# Install Maven
RUN apt-get update
RUN apt-get -y install curl tar bash
ARG MAVEN_VERSION=3.3.9
ARG USER_HOME_DIR="/root"
RUN mkdir -p /usr/share/maven && \
curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1 && \
ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

COPY ./src ./src
COPY ./pom.xml ./pom.xml

RUN mvn package -Dmaven.test.skip=true
RUN rm -rf /usr/share/maven
RUN rm -rf /root/.m2
RUN rm -rf /src
RUN rm pom.xml
RUN apt-get remove purge curl tar bash
EXPOSE 8080 8443

CMD java -jar /target/AuthService.jar

