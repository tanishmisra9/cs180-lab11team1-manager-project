package src;

public interface Client {

    void connect(String host, int port);

    void disconnect();

    boolean isConnected();


    void sendRequest(ClientRequest request);

    void addListener(ClientListener listener);

    void removeListener(ClientListener listener);
}

