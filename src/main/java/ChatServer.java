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
            System.out.println(">>> MongoDB подключена!");
        } catch (Exception e) {
            System.err.println("!!! Ошибка БД: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Новое подключение");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JSONObject json = new JSONObject(message);
        if (json.getString("type").equals("register")) {
            String email = json.getString("email");
            String pass = json.getString("password");
            Document user = usersCollection.find(Filters.eq("email", email)).first();

            if (user == null) {
                usersCollection.insertOne(new Document("email", email).append("password", pass));
                sendSystemMessage(conn, "Регистрация успешна!");
            } else if (user.getString("password").equals(pass)) {
                sendSystemMessage(conn, "Вход выполнен!");
            } else {
                sendSystemMessage(conn, "Неверный пароль!");
            }
        } else {
            broadcast(message);
        }
    }

    private void sendSystemMessage(WebSocket conn, String text) {
        JSONObject resp = new JSONObject();
        resp.put("type", "chat");
        resp.put("user", "Система");
        resp.put("text", text);
        conn.send(resp.toString());
    }

    @Override public void onClose(WebSocket conn, int code, String reason, boolean remote) {}
    @Override public void onError(WebSocket conn, Exception ex) {}
    @Override public void onStart() { System.out.println("Сервер запущен!"); }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        new ChatServer(port).start();
    }
}