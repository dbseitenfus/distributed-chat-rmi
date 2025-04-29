import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class RoomChat extends UnicastRemoteObject implements IRoomChat {
  private String roomName;
  private Map<String, IUserChat> userList;

  public RoomChat(String roomName) throws RemoteException {
    this.roomName = roomName;
    userList = new HashMap<>();
  }

  @Override
  public void sendMsg(String userName, String msg) throws RemoteException {
    for (IUserChat user : userList.values()) {
      user.deliverMsg(userName, msg);
    }
  }

  @Override
  public void joinRoom(String userName, IUserChat user) throws RemoteException{
    userList.put(userName, user);
    sendMsg("Sistema", userName + " entrou na sala.");
  }

  @Override
  public void leaveRoom(String userName) throws RemoteException{
    userList.remove(userName);
    sendMsg("Sistema", userName + " saiu da sala.");
  }

  @Override
  public String getRoomName() throws RemoteException{
    return roomName;
  }

  @Override
  public void closeRoom() throws RemoteException{
    for (IUserChat user : userList.values()) {
      user.deliverMsg("Sistema", "A sala foi fechada.");
    }
    userList.clear();
  }
}
