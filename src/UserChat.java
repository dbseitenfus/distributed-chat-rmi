import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class UserChat extends UnicastRemoteObject implements IUserChat {
    public String userName;
    private IRoomChat currentRoom;
    private IServerChat server;
    private JTextArea chatArea;

    String ip = "10.1.1.91";
    int port = 2020;

    public UserChat(String userName) throws RemoteException {
        this.userName = userName;
        setupGUI();
        connectToServer();
        chooseRoom();
    }

    private void setupGUI() {
        JFrame frame = new JFrame("Chat - " + userName);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JTextField msgField = new JTextField(20);
        JButton sendBtn = new JButton("SEND");
        JButton joinBtn = new JButton("JOIN");
        JButton leaveBtn = new JButton("LEAVE");

        panel.add(msgField);
        panel.add(sendBtn);
        panel.add(joinBtn);
        panel.add(leaveBtn);

        frame.add(panel, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> {
           sendMessage(msgField.getText());
            msgField.setText("");
        });

        joinBtn.addActionListener(e -> chooseRoom());

        leaveBtn.addActionListener(e -> leaveRoom());

        frame.setVisible(true);
    }

    private void sendMessage(String msg)  {
        if (!msg.isEmpty() && currentRoom != null) {
            currentRoom.sendMsg(userName, msg);

        }
    }

    private void chooseRoom() {
        List<String> rooms = server.getRooms();

        String[] roomOptions = new String[rooms.size() + 1];
        roomOptions[0] = "<Criar nova sala>";
        for (int i = 0; i < rooms.size(); i++) {
            roomOptions[i + 1] = rooms.get(i);
        }

        String selectedOption = (String) JOptionPane.showInputDialog(
                null,
                "Escolha uma sala ou crie uma nova:",
                "Salas disponíveis",
                JOptionPane.PLAIN_MESSAGE,
                null,
                roomOptions,
                roomOptions[0]
        );

        if (selectedOption == null) return;

        String roomName;

        if (selectedOption.equals("<Criar nova sala>")) {
            roomName = JOptionPane.showInputDialog("Digite o nome da nova sala:");
            if (roomName == null || roomName.trim().isEmpty()) return;
            roomName = roomName.trim();

            if (rooms.contains(roomName)) {
                JOptionPane.showMessageDialog(null, "Já existe uma sala com esse nome.");
                return;
            }

            server.createRoom(roomName);
        } else {
            roomName = selectedOption;
        }

        if (currentRoom != null) {
            leaveRoom();
        }

        joinRoom(roomName);

    }

    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            server = (IServerChat) registry.lookup("Servidor");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void joinRoom(String roomName) {
        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            currentRoom = (IRoomChat) registry.lookup(roomName);
            currentRoom.joinRoom(userName, this);
            chatArea.append("Entrou na sala: " + roomName + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void leaveRoom() {
        try {
            if (currentRoom != null) {
                currentRoom.leaveRoom(userName);
                chatArea.append("Saiu da sala.\n");
                currentRoom = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliverMsg(String senderName, String msg){
        chatArea.append(senderName + ": " + msg + "\n");
    }
}
