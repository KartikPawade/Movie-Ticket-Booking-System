FROM maven:3.8.3-openjdk-17 as stage1

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY ./src ./src

RUN mvn clean install


# stage-2
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=stage1 /app/target/movie-ticket-booking-system.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]