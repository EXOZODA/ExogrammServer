# Используем образ с Maven для сборки
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Используем легкий образ Java для запуска
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/EXOGRAMM-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]