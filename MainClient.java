

public class MainClient {
  public static void main(String[] args) {
    try {
      String userName = javax.swing.JOptionPane.showInputDialog("Seu nome:");
      new UserChat(userName);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
