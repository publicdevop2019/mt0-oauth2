# Project Status
![Docker Cloud Automated build](https://img.shields.io/docker/cloud/automated/publicdevop2019/oauth2service.svg?style=flat-square)  ![Docker Cloud Build Status](https://img.shields.io/docker/cloud/build/publicdevop2019/oauth2service.svg?style=flat-square)  ![Docker Pulls](https://img.shields.io/docker/pulls/publicdevop2019/oauth2service.svg?style=flat-square)  
![Sonar Coverage](https://img.shields.io/sonar/https/sonarcloud.io/com.hw%3AOAuth2Service/coverage.svg?style=flat-square)  ![Sonar Quality Gate](https://img.shields.io/sonar/https/sonarcloud.io/com.hw%3AOAuth2Service/quality_gate.svg?style=flat-square)  
![GitHub last commit](https://img.shields.io/github/last-commit/publicdevop2019/oauth2service.svg?style=flat-square)  
# Swagger Editor
**copy content in swagger-editor.yaml to get start** 

For http, please use [**this**](http://editor.swagger.io) http://editor.swagger.io swagger editor http url to avoid mix content error   

For https, please use [**this**](https://editor.swagger.io) https://editor.swagger.io   
# DockerHub
[DockerHub repo link](https://hub.docker.com/r/publicdevop2019/oauth2service)
# Deploy with docker
**Simply pull the latest image and run!**  
```shell
docker pull publicdevop2019/oauth2service:latest  
docker run -td --rm -p 8080:8080 --name oauth2service publicdevop2019/oauth2service:latest  
```
# Sonar Cloud command
```shell
docker run -it --rm -v ~/oauth2service:/usr/src/app -v ~/.m2:/root/.m2 -w /usr/src/app maven:3.6.0-jdk-11 mvn clean verify sonar:sonar -Dsonar.projectKey=com.hw:OAuth2Service -Dsonar.organization=publicdevop2019-github -Dsonar.host.url=https://sonarcloud.io -Dsonar.login={generatedLoginID}
```
