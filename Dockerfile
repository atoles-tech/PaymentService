FROM eclipse-temurin:21-jdk-alpine

COPY /target/payment_service-0.0.1-SNAPSHOT.jar /payment_service/payment-service.jar

WORKDIR /payment_service

EXPOSE 8086

ENTRYPOINT [ "java","-jar","payment-service.jar", "--spring.profiles.active=docker" ]