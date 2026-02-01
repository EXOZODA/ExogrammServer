import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;

public class MessengerApp {
    private static WebSocketClient client;
    private static String userEmail;
    private static JPanel chatPanel;

    public static void main(String[] args) {
        showAuthWindow();
    }

    private static void showAuthWindow() {
        JFrame authFrame = new JFrame("Exogramm - Вход");
        authFrame.setSize(350, 400);
        authFrame.setLayout(new GridLayout(6, 1, 10, 10));
        authFrame.setLocationRelativeTo(null);

        JTextField emailField = new JTextField("Email");
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Войти / Зарегистрироваться");

        authFrame.add(new JLabel("Добро пожаловать!", SwingConstants.CENTER));
        authFrame.add(emailField);
        authFrame.add(new JLabel("Пароль:"));
        authFrame.add(passField);
        authFrame.add(loginBtn);

        loginBtn.addActionListener(e -> {
            userEmail = emailField.getText();
            String password = new String(passField.getPassword());
            if (!userEmail.isEmpty() && !password.isEmpty()) {
                authFrame.dispose();
                openChatWindow(userEmail, password);
            }
        });

        authFrame.setVisible(true);
    }

    private static void openChatWindow(String email, String pass) {
        JFrame frame = new JFrame("Exogramm — " + email);
        frame.setSize(450, 600);
        frame.setLocationRelativeTo(null);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(chatPanel);

        JTextField textField = new JTextField();
        JButton sendButton = new JButton("➤");

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        try {
            URI uri = new URI("ws://exogrammserver.onrender.com");
            client = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakeData) {
                    JSONObject reg = new JSONObject();
                    reg.put("type", "register");
                    reg.put("email", email);
                    reg.put("password", pass);
                    send(reg.toString());
                    renderMessage("Система: Вы в сети!", false);
                }

                @Override
                public void onMessage(String message) {
                    JSONObject json = new JSONObject(message);
                    if (json.getString("type").equals("chat")) {
                        renderMessage(json.getString("user") + ": " + json.getString("text"), false);
                    }
                }

                @Override public void onClose(int code, String reason, boolean remote) {}
                @Override
                public void onError(Exception ex) {
                    System.err.println("Ошибка WebSocket: " + ex.getMessage());
                    renderMessage("Ошибка: " + ex.getMessage(), false);
                }
            };
            client.connect();
        } catch (Exception e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }

        sendButton.addActionListener(e -> {
            String text = textField.getText();
            if (!text.isEmpty() && client.isOpen()) {
                JSONObject msg = new JSONObject();
                msg.put("type", "chat");
                msg.put("user", email);
                msg.put("text", text);
                client.send(msg.toString());
                renderMessage("Вы: " + text, true);
                textField.setText("");
            }
        });

        frame.setVisible(true);
    }

    private static void renderMessage(String text, boolean isOwn) {
        SwingUtilities.invokeLater(() -> {
            JPanel msgContainer = new JPanel(new FlowLayout(isOwn ? FlowLayout.TRAILING : FlowLayout.LEADING));
            msgContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            JLabel label = new JLabel(text);
            label.setOpaque(true);
            label.setBackground(isOwn ? new Color(200, 255, 200) : new Color(230, 230, 230));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            msgContainer.add(label);
            chatPanel.add(msgContainer);
            chatPanel.revalidate();
            chatPanel.repaint();
        });
    }
}