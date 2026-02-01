# Используем стабильный образ Java 17
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Копируем файлы проекта
COPY . .

# Компилируем сервер
RUN javac src/main/java/ChatServer.java

# Запускаем сервер, указывая папку с классами
CMD ["java", "-cp", "src/main/java", "ChatServer"]