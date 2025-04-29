import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainServer {
  public static void main(String[] args) {
    try {
      ServerChat server = new ServerChat();
      Registry registry = LocateRegistry.createRegistry(2020);
      registry.rebind("Servidor", server);
      System.out.println("Servidor pronto.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
