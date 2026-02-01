import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    public static void main(String[] args) {
        // Render передает порт через переменную окружения PORT
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новое подключение!");

                // Простейшая обработка сообщений
                new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                        String message;
                        while ((message = in.readLine()) != null) {
                            System.out.println("Получено: " + message);
                            out.println("Эхо: " + message);
                        }
                    } catch (IOException e) {
                        System.out.println("Связь разорвана");
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}