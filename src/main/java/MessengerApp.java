import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class MessengerApp {
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Exogramm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollChat = new JScrollPane(chatArea);

        JTextField textField = new JTextField();
        JButton sendButton = new JButton("Отправить");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(scrollChat, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // ПОДКЛЮЧЕНИЕ К ТВОЕМУ СЕРВЕРУ НА RENDER
        new Thread(() -> {
            try {
                // Render использует стандартный порт 80 для HTTP (или 443 для HTTPS)
                // Мы подключаемся по обычному сокету к адресу, который ты дал
                String host = "exogrammserver.onrender.com";
                int port = 80;

                Socket socket = new Socket(host, port);
                chatArea.append("Подключено к серверу!\n");

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String line;
                while ((line = in.readLine()) != null) {
                    chatArea.append("Сервер: " + line + "\n");
                }
            } catch (Exception e) {
                chatArea.append("Ошибка подключения: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }).start();

        ActionListener sendAction = e -> {
            String text = textField.getText();
            if (!text.isEmpty() && out != null) {
                out.println(text);
                chatArea.append("Вы: " + text + "\n");
                textField.setText("");
            }
        };

        sendButton.addActionListener(sendAction);
        textField.addActionListener(sendAction);

        frame.setVisible(true);
    }
}