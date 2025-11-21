package src;
public class ClientA {

    private final Client client;

    public ClientA(Client client) {
        this.client = client;
    }

    public void connectToServer() {
        client.connect("localhost", 4444);
    }

    public void login(String username, String password) {
        ClientRequest req = new ClientRequest(
                "LOGIN",
                new LoginPayload(username, password)
        );
        client.sendRequest(req);
    }

    public void sendChatMessage(String msg) {
        ClientRequest req = new ClientRequest("MESSAGE", msg);
        client.sendRequest(req);
    }

    public void logout() {
        client.sendRequest(new ClientRequest("LOGOUT", null));
    }
}
