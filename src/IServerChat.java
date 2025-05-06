import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IServerChat extends java.rmi.Remote {
    public ArrayList<String> getRooms();
    public void createRoom(String roomName);
}