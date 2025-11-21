package src;
public interface ClientListener {
    void onConnected();
    void onDisconnected();
    void onResponseReceived(ServerResponse response);
    void onError(String message, Exception e);
}
