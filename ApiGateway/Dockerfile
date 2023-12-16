FROM openjdk:17-jdk-alpine
EXPOSE 8083
ARG GOOGLE_CLIENT_ID
ARG GOOGLE_CLIENT_SECRET
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]