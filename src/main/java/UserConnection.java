import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class UserConnection {

    private final Socket socket;
    private final Thread receiveThread;
    private final EventListener eventListener;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public UserConnection(EventListener eventListener, String ipAddress, int port) throws IOException {
        this(eventListener, new Socket(ipAddress, port));
    }

    public UserConnection(EventListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(UserConnection.this);
                    while (!receiveThread.isInterrupted()) {
                        eventListener.onReceiveMessage(UserConnection.this, reader.readLine());
                    }

                } catch (IOException e) {
                    eventListener.onException(UserConnection.this, e);
                } finally {
                    eventListener.onDisconnect(UserConnection.this);
                }
            }

        });
        receiveThread.start();
    }

    public synchronized void sendString(String value) {
        try {
            writer.write(value + "\r\n");
            writer.flush();
        } catch (IOException e) {
            eventListener.onException(UserConnection.this, e);
            disconnect();
        }

    }

    public synchronized void disconnect() {
        receiveThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(UserConnection.this, e);
        }

    }


    @Override
    public String toString() {
        return socket.getInetAddress() + ": " + socket.getPort();
    }
}
