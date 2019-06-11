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
