FROM openjdk:17-ea-11-jdk-slim
VOLUME /temp
COPY target/user-service-1.0.jar UserService.jar
ENTRYPOINT ["java","-jar","UserService.jar"]