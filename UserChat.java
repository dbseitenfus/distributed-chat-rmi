import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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

  public UserChat(String userName) throws RemoteException {
    this.userName = userName;
    setupGUI();
    connectToServer();
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
        try {
            if (currentRoom != null) {
                currentRoom.sendMsg(userName, msgField.getText());
                msgField.setText("");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });

      joinBtn.addActionListener(e -> {
          String roomName = JOptionPane.showInputDialog("Nome da sala:");
          joinRoom(roomName);
      });

      leaveBtn.addActionListener(e -> {
          try {
              if (currentRoom != null) {
                  currentRoom.leaveRoom(userName);
                  currentRoom = null;
                  chatArea.append("Você saiu da sala.\n");
              }
          } catch (Exception ex) {
              ex.printStackTrace();
          }
      });

      frame.setVisible(true);
  }

  private void connectToServer() {
    try {
      Registry registry = LocateRegistry.getRegistry("localhost", 2020);
      server = (IServerChat) registry.lookup("Servidor");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void joinRoom(String roomName) {
    try {
      server.createRoom(roomName);
      Registry registry = LocateRegistry.getRegistry("localhost", 2020);
      currentRoom = (IRoomChat) registry.lookup(roomName);
      currentRoom.joinRoom(userName, this);
      chatArea.append("Entrou na sala: " + roomName + "\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deliverMsg(String senderName, String msg) throws RemoteException{
    System.out.println("[" + userName + "] " + senderName + ": " + msg);
  }
}
