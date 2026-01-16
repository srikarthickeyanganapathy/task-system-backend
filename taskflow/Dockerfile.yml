# 1. Build Stage (Use Maven with Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Run Stage (Use JDK 21 Slim)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/taskflow-0.0.1-SNAPSHOT.jar taskflow.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","taskflow.jar"]