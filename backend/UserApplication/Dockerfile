FROM openjdk:17-jdk-alpine
EXPOSE 8081
ARG DB_NAME
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]