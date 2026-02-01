import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessengerApp {
    public static void main(String[] args) {
        // 1. Создаем окно
        JFrame frame = new JFrame("Pro Messenger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        // 2. Список чатов слева
        String[] chats = {"Мама", "Артем (код)", "Группа проекта", "Пицца-бот"};
        JList<String> chatList = new JList<>(chats);
        chatList.setFixedCellWidth(150);

        // 3. Область сообщений (центр)
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true); // Перенос строк
        JScrollPane scrollChat = new JScrollPane(chatArea);

        // 4. Панель ввода (низ)
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField textField = new JTextField(); // Поле для текста
        JButton sendButton = new JButton("Отправить"); // Кнопка

        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // 5. ЛОГИКА: Что происходит при нажатии кнопки
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (!text.isEmpty()) {
                    chatArea.append("Вы: " + text + "\n"); // Добавляем текст в окно
                    textField.setText(""); // Очищаем поле ввода
                }
            }
        };

        sendButton.addActionListener(sendAction);
        textField.addActionListener(sendAction); // Чтобы работало по нажатию Enter

        // 6. Сборка интерфейса
        frame.getContentPane().add(new JScrollPane(chatList), BorderLayout.WEST);
        frame.getContentPane().add(scrollChat, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        // 7. Запуск
        frame.setVisible(true);
    }
}