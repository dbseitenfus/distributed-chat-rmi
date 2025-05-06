import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ServerChat extends UnicastRemoteObject implements IServerChat {
    private Map<String, IRoomChat> roomList;

    //GUI
    private DefaultTableModel tableModel;


    public ServerChat() throws RemoteException {
        roomList = new HashMap<>();
        setupGUI();
    }

    private void setupGUI() {
        JFrame frame = new JFrame("ServerChat");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] columnNames = {"Sala"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Fechar Sala Selecionada");
        closeButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String roomName = (String) tableModel.getValueAt(selectedRow, 0);
                System.out.println("Sala selecionada: " + selectedRow + ", nome: " + roomName);
                closeRoom(roomName);
            } else {
                JOptionPane.showMessageDialog(frame, "Selecione uma sala para fechar.");
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(closeButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        updateRoomTable();
    }

    private void closeRoom(String roomName) {
        try {
            IRoomChat room = roomList.get(roomName);
            if (room != null) {
                room.closeRoom();
                roomList.remove(roomName);
                Registry registry = LocateRegistry.getRegistry("192.168.0.105", 2020);
                registry.unbind(roomName);
                System.out.println("Sala fechada via RMI: " + roomName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateRoomTable();
    }

    private void updateRoomTable() {
        tableModel.setRowCount(0);
        for (String roomName : roomList.keySet()) {
            tableModel.addRow(new Object[]{roomName});
        }
    }

    @Override
    public ArrayList<String> getRooms() {
        return new ArrayList<>(roomList.keySet());
    }

    @Override
    public synchronized void createRoom(String roomName)  {
        if (!roomList.containsKey(roomName)) {
            try {
                RoomChat room = new RoomChat(roomName);
                Registry registry = LocateRegistry.getRegistry("192.168.0.105", 2020);
                registry.rebind(roomName, room);
                roomList.put(roomName, room);
                System.out.println("Sala criada e registrada: " + roomName);
                updateRoomTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
