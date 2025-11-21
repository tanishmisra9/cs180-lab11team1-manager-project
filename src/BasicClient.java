package src;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BasicClient implements Client {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final List<ClientListener> listeners = new CopyOnWriteArrayList<>();

    private volatile boolean connected = false;

    @Override
    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            connected = true;
            fireConnected();

            new Thread(this::listenLoop).start();

        } catch (Exception e) {
            fireError("Connection failed", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            connected = false;

            if (socket != null) socket.close();
            fireDisconnected();

        } catch (Exception e) {
            fireError("Disconnect error", e);
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void sendRequest(ClientRequest request) {
        try {
            out.writeObject(request);
            out.flush();
        } catch (Exception e) {
            fireError("Failed to send request", e);
        }
    }

    @Override
    public void addListener(ClientListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ClientListener listener) {
        listeners.remove(listener);
    }

    private void listenLoop() {
        try {
            while (connected) {
                Object obj = in.readObject();
                if (obj instanceof ServerResponse) {
                    fireResponse((ServerResponse) obj);
                }
            }
        } catch (Exception e) {
            fireError("Listening failed", e);
            disconnect();
        }
    }

    private void fireConnected() {
        listeners.forEach(ClientListener::onConnected);
    }

    private void fireDisconnected() {
        listeners.forEach(ClientListener::onDisconnected);
    }

    private void fireResponse(ServerResponse response) {
        listeners.forEach(l -> l.onResponseReceived(response));
    }

    private void fireError(String msg, Exception e) {
        listeners.forEach(l -> l.onError(msg, e));
    }
}
