package src;
//confirming it works
public class ClientTest {
    public static void main(String[] args) {

        Client basic = new BasicClient();

        ClientService a = new ClientService(basic);
      
        a.connectToServer();
        a.login("sravya", "password123");
        a.sendChatMessage("Hello world!");
    }
}
