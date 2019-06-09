FROM openjdk:11-jre-slim

LABEL maintainer="publicdevop2019hw@gmail.com"

ENV MAVEN_VERSION 3.6.1

RUN mkdir -p /usr/share/maven \
  && curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
    | tar -xzC /usr/share/maven --strip-components=1 \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

VOLUME /root/.m2

RUN mvn clean install -Dmaven.test.skip=true

EXPOSE 8443

CMD java -jar OAuth2Service.jar
