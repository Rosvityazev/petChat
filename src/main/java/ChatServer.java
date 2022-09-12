import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements EventListener {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<UserConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            while (true) {
                try {
                    new UserConnection(this, serverSocket.accept());
                } catch (Exception e) {
                    System.out.println("UserConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(UserConnection userConnection) {
        connections.add(userConnection);
        sendToAllUsers("Client connected: " + userConnection);

    }

    @Override
    public synchronized void onReceiveMessage(UserConnection userConnection, String value) {
        sendToAllUsers(value);

    }

    @Override
    public synchronized void onDisconnect(UserConnection userConnection) {
        connections.remove(userConnection);
        sendToAllUsers("Client disconnected: " + userConnection);

    }

    @Override
    public synchronized void onException(UserConnection userConnection, Exception e) {
        System.out.println("UserConnection exception: " + e);
    }

    private void sendToAllUsers(String value) {
        System.out.println(value);
        for (UserConnection connection : connections) connection.sendString(value);
    }
}

