import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.net.InetSocketAddress;

public class ChatServer extends WebSocketServer {
    private static MongoCollection<Document> usersCollection;

    public ChatServer(int port) {
        super(new InetSocketAddress(port));
        try {
            // Твоя ссылка из MongoDB Atlas
            String connectionString = "mongodb+srv://exozoda_db_user:ZTiXr6lp6Mk40zr8@exozoda.n9uxxlh.mongodb.net/?appName=EXOZODA";

            MongoClient mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase("ExogrammDB");
            usersCollection = database.getCollection("users");
            System.out.println(">>> Успешно подключено к MongoDB Atlas!");
        } catch (Exception e) {
            System.err.println("!!! Ошибка подключения к БД: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Новый клиент в сети: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JSONObject json = new JSONObject(message);
            String type = json.getString("type");

            if (type.equals("register")) {
                String email = json.getString("email");
                String pass = json.getString("password");

                Document user = usersCollection.find(Filters.eq("email", email)).first();

                if (user == null) {
                    usersCollection.insertOne(new Document("email", email).append("password", pass));
                    sendSystemMessage(conn, "Аккаунт создан и сохранен в БД!");
                } else if (user.getString("password").equals(pass)) {
                    sendSystemMessage(conn, "Вход выполнен успешно!");
                } else {
                    sendSystemMessage(conn, "Ошибка: неверный пароль!");
                }
            } else if (type.equals("chat")) {
                broadcast(message);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки данных: " + e.getMessage());
        }
    }

    private void sendSystemMessage(WebSocket conn, String text) {
        conn.send(new JSONObject()
                .put("type", "chat")
                .put("user", "Система")
                .put("text", text)
                .toString());
    }

    @Override public void onClose(WebSocket conn, int code, String reason, boolean remote) {}
    @Override public void onError(WebSocket conn, Exception ex) {}
    @Override public void onStart() { System.out.println("Сервер Exogramm активен на порту: " + getPort()); }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        new ChatServer(port).start();
    }
}