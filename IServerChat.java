import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServerChat extends Remote {
  public ArrayList<String> getRooms();
  public void createRoom(String roomName);
}