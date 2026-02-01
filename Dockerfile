# Используем образ с Maven для сборки
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Используем стабильный и поддерживаемый образ Eclipse Temurin для запуска
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Копируем созданный JAR из сборщика
COPY --from=builder /app/target/EXOGRAMM-1.0-SNAPSHOT.jar app.jar

# Render передает порт через переменную среды
EXPOSE 8080

# Запускаем приложение
CMD ["java", "-jar", "app.jar"]