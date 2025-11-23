package src;

public class ClientService {

    private final Client client;

    public ClientService(Client client) {
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

    public void sendMessage(String msg) {
        ClientRequest req = new ClientRequest("MESSAGE", msg);
        client.sendRequest(req);
    }

    public void logout() {
        client.sendRequest(new ClientRequest("LOGOUT", null));
    }

    public void reserveSeats(String movie, String showtime, int startRow,
                             int startSeat, int numPeople, double fee) {
        ReservationPayload payload = new ReservationPayload(movie, showtime, startRow, startSeat, numPeople, fee);
        client.sendRequest(new ClientRequest("RESERVE_SEAT", payload));
    }

    public void cancelReservation(String movie, String showtime, int numPeople, double fee) {
        CancelPayload payload = new CancelPayload(movie, showtime, numPeople, fee);
        client.sendRequest(new ClientRequest("CANCEL_RESERVATION", payload));
    }
}
