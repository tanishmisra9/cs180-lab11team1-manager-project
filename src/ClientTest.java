package src;

public class ClientTest {
    public static void main(String[] args) {

        Client basic = new BasicClient();

        ClientA a = new ClientA(basic);
      
        a.connectToServer();
        a.login("sravya", "password123");
        a.sendChatMessage("Hello world!");
    }
}
