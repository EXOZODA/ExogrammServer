FROM openjdk:17-jdk-slim
WORKDIR /app
# Копируем всё содержимое проекта
COPY . .
# Компилируем только серверный файл, указывая путь к нему
RUN javac src/main/java/ChatServer.java
# Запускаем его из той же папки, где он лежит
CMD ["java", "-cp", "src/main/java", "ChatServer"]