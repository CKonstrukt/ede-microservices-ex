FROM openjdk:17-jdk-alpine
EXPOSE 8080
ARG DB_NAME
ARG DB_USER
ARG DB_PASSWORD
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]