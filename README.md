# Swagger Editor
![Docker Cloud Automated build](https://img.shields.io/docker/cloud/automated/publicdevop2019/oauth2service.svg?style=flat-square)  ![Docker Cloud Build Status](https://img.shields.io/docker/cloud/build/publicdevop2019/oauth2service.svg?style=flat-square)  ![Docker Pulls](https://img.shields.io/docker/pulls/publicdevop2019/oauth2service.svg?style=flat-square)  
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
