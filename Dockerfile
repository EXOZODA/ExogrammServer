# Используем образ с Java 17
FROM openjdk:17-jdk-slim

# Копируем файлы проекта в контейнер
COPY . /app
WORKDIR /app

# Собираем проект через Maven (он уже встроен в образ или используем mvnw)
RUN ./mvnw clean package

# Открываем порт, который мы указали в ChatServer (12345)
EXPOSE 12345

# Запускаем сервер
CMD ["java", "-cp", "target/EXOGRAMM-1.0-SNAPSHOT.jar", "ChatServer"]