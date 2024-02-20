FROM openjdk:17-jdk-slim
EXPOSE 8080
WORKDIR /app
COPY target/Movie-ticket-booking-system.jar .
ENTRYPOINT ["java","-jar","Movie-ticket-booking-system.jar"]