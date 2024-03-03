FROM openjdk:17

WORKDIR /app

COPY target/movie-ticket-booking-system.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]