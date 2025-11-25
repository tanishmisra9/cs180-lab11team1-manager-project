package src;
//import src.AdminPayloads.*;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientService {

    private final BasicClient client;
    private final BlockingQueue<ServerResponse> responseQueue = new LinkedBlockingQueue<>();

    public ClientService(BasicClient client) {
        this.client = client;
        // Add a listener to push incoming responses into the queue
        client.addListener(new ClientListener() {
            @Override
            public void onConnected() {}

            @Override
            public void onDisconnected() {}

            @Override
            public void onResponseReceived(ServerResponse response) {
                responseQueue.offer(response);
            }

            @Override
            public void onError(String msg, Exception e) {}
        });
    }

    public void connectToServer() {
        client.connect("localhost", 4242);
    }

    // --------------------------
    // LOGIN / REGISTER
    // --------------------------
    public void login(String username, String password) {
        ClientRequest req = new ClientRequest(
                "LOGIN",
                new LoginPayload(username, password)
        );
        client.sendRequest(req);
    }

    public void register(String username, String password) {
        RegistrationPayload payload = new RegistrationPayload(username, password);
        ClientRequest req = new ClientRequest("REGISTER", payload);
        client.sendRequest(req);
    }

    // --------------------------
    // MESSAGES / LOGOUT
    // --------------------------
    public void sendMessage(String msg) {
        ClientRequest req = new ClientRequest("MESSAGE", msg);
        client.sendRequest(req);
    }

    public void logout() {
        client.sendRequest(new ClientRequest("LOGOUT", null));
    }

    // --------------------------
    // USER SEAT / MOVIE FLOW
    // --------------------------
    public void requestMovies() {
        ClientRequest req = new ClientRequest("REQUEST_MOVIES", null);
        client.sendRequest(req);
    }

    public void requestAuditorium(String movieName) {
        RequestAuditoriumPayload payload = new RequestAuditoriumPayload(movieName);
        ClientRequest req = new ClientRequest("REQUEST_AUDITORIUM", payload);
        client.sendRequest(req);
    }

    public void reserveSeat(String movieName, int row, int col, String username) {
        ReserveSeatPayload payload = new ReserveSeatPayload(movieName, row, col, username);
        ClientRequest req = new ClientRequest("RESERVE_SEAT", payload);
        client.sendRequest(req);
    }

    // --------------------------
    // ADMIN SHOWING MANAGEMENT
    // --------------------------
    public void editShowingName(String oldName, String newName, LocalDateTime oldtime) {
        EditShowingNamePayload payload = new EditShowingNamePayload(oldName, newName, oldtime);
        ClientRequest req = new ClientRequest("EDIT_SHOWING_NAME", payload);
        client.sendRequest(req);
    }

    public void editShowingTime(String movieName, LocalDateTime newTime, LocalDateTime oldtime) {
        EditShowingTimePayload payload = new EditShowingTimePayload(movieName, newTime, oldtime);
        ClientRequest req = new ClientRequest("EDIT_SHOWING_TIME", payload);
        client.sendRequest(req);
    }

    public void cancelShowing(String movieName) {
        CancelShowingPayload payload = new CancelShowingPayload(movieName, null);
        ClientRequest req = new ClientRequest("CANCEL_SHOWING", payload);
        client.sendRequest(req);
    }

    public void createVenue(String venueName, int rows, int cols, String showingName, LocalDateTime time, double price) {
        CreateVenuePayload payload = new CreateVenuePayload(venueName, rows, cols, showingName, time, price);
        ClientRequest req = new ClientRequest("CREATE_VENUE", payload);
        client.sendRequest(req);
    }

        /** Blocking method to get the next ServerResponse */
        public ServerResponse receiveResponse() {
            try {
                System.out.println("waiting...");
                return responseQueue.take(); // waits until a response is available
               
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

}
