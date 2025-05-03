import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
    public List<String> getRooms() throws RemoteException {
        return new ArrayList<>(roomList.keySet());
    }

    @Override
    public synchronized void createRoom(String roomName) throws RemoteException {
        if (!roomList.containsKey(roomName)) {
            RoomChat room = new RoomChat(roomName);
            try {
                Registry registry = LocateRegistry.getRegistry(2020);
                registry.rebind(roomName, room);
                roomList.put(roomName, room);
                System.out.println("Sala criada e registrada: " + roomName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
