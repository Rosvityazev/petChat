import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class UserWindow extends JFrame implements ActionListener, EventListener {

    private static final String IP_ADDRESS = "172.29.64.1";
    private static final int PORT = 9000;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserWindow::new);

    }
    private final JTextArea log = new JTextArea();
    private final JTextField fieldLogin = new JTextField("Enter your login");
    private final JTextField fieldMessage = new JTextField();

    private UserConnection connection;
    private UserWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        log.setEditable(false);
        fieldMessage.addActionListener(this);
        add(log, BorderLayout.CENTER);
        add(fieldMessage, BorderLayout.SOUTH);
        add(fieldLogin, BorderLayout.NORTH);
        setVisible(true);
        try {
            connection = new UserConnection(this,IP_ADDRESS, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldMessage.getText();
        if(message.equals("")) return;
        fieldMessage.setText(null);
        connection.sendString(fieldLogin.getText() + ":" + message);

    }

    @Override
    public void onConnectionReady(UserConnection userConnection) {
        printMessage("Connection ready...");

    }

    @Override
    public void onReceiveMessage(UserConnection userConnection, String value) {
        printMessage(value);

    }

    @Override
    public void onDisconnect(UserConnection userConnection) {
        printMessage("Connection closed...");

    }

    @Override
    public void onException(UserConnection userConnection, Exception e) {
        printMessage("Connection exception: " + e);

    }

    private synchronized void printMessage(String Message) {
        SwingUtilities.invokeLater(() -> {
            log.append(Message + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}
