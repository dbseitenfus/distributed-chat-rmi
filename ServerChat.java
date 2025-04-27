import interfaces.IRoomChat;
import interfaces.IServerChat;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ServerChat extends UnicastRemoteObject implements IServerChat {
  private Map<String, IRoomChat> roomList;

  public ServerChat() throws RemoteException {
    roomList = new HashMap<>();
  }

  @Override
  public ArrayList<String> getRooms() throws RemoteException {
    return new ArrayList<>(roomList.keySet());
  }

  @Override
  public void createRoom(String roomName) throws RemoteException {
    if (!roomList.containsKey(roomName)) {
      IRoomChat room = new RoomChat(roomName);
      roomList.put(roomName, room);
    } else {
      throw new RemoteException("Room already exists");
    }
  }
}
