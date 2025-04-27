import interfaces.IServerChat;
import interfaces.IUserChat;
import interfaces.IRoomChat;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import javax.swing.JTextArea;

public class UserChat extends UnicastRemoteObject implements IUserChat {
  public String userName;
  private IRoomChat currentRoom;
  private IServerChat server;
  private JTextArea chatArea;

  public UserChat(String userName) throws RemoteException {
    this.userName = userName;
  }

  @Override
  public void deliverMsg(String senderName, String msg) throws RemoteException {
    System.out.println("[" + userName + "] " + senderName + ": " + msg);
  }
}
