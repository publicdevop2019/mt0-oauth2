# Project Status
![Docker Cloud Automated build](https://img.shields.io/docker/cloud/automated/publicdevop2019/oauth2service.svg?style=flat-square)  ![Docker Cloud Build Status](https://img.shields.io/docker/cloud/build/publicdevop2019/oauth2service.svg?style=flat-square)  ![Docker Pulls](https://img.shields.io/docker/pulls/publicdevop2019/oauth2service.svg?style=flat-square)  
![Sonar Coverage](https://img.shields.io/sonar/https/sonarcloud.io/com.hw%3Aoauth2/coverage.svg?style=flat-square)  ![Sonar Quality Gate](https://img.shields.io/sonar/https/sonarcloud.io/com.hw%3Aoauth2/quality_gate.svg?style=flat-square)  
![GitHub last commit](https://img.shields.io/github/last-commit/publicdevop2019/oauth2service.svg?style=flat-square)
# Feature
- Based on spring-security-oauth2-autoconfigure
- JWT asymmetric key validation, mt1-proxy retrieve public key on start up
- Expose authorize code endpoint as separate API
- Support client credential, password, authorization, refresh token flow
- Forget password support, two-step user registration
- JWT token blacklist (together with mt1-proxy)
- Async logging with graceful shutdown
# Misc
- Swagger Document (Generated): http://localhost:8080/swagger-ui.html
- DockerHub: https://hub.docker.com/r/publicdevop2019/oauth2service
# 中文
- 基于 spring-security-oauth2-autoconfigure
- JWT不对称钥匙验证, mt1-proxy启动自动获取公匙
- 开放第三方 authorize code API
- 支持 client credential, password, authorization, refresh token 流程
- 忘记密码重置, 注册码注册用户
- JWT (与 mt1-proxy一起使用) 主动以及被动回收
- 异步日志以及优雅关机（覆盖全部服务）