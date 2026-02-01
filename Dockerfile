# Используем образ Maven для сборки
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Используем легкий образ Java для запуска
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/EXOGRAMM-1.0-SNAPSHOT.jar app.jar

# Render сам назначит порт через переменную среды PORT
EXPOSE 8080

# Запускаем сервер, передавая порт из системы
CMD ["java", "-jar", "app.jar"]