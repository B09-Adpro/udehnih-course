FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/udehnih-course.jar udehnih-course.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "udehnih-course.jar"]