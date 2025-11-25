package src;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Server implements ServerInterface, Runnable {
    private boolean exit = false;
    private ReservationDatabase database;

    public Server() {
        this.database = ReservationDatabase.loadDatabase();
        this.database.populateDefaults();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(4242))  {
            while (!exit) { //server loop
                System.out.println("Waiting for client to connect...");

                try (Socket socket = serverSocket.accept();
                     ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream reader = new ObjectInputStream(socket.getInputStream())) {

                    System.out.println("Client connected!");

                    while (true) { // individual client loop
                        ClientRequest req = ServerInterface.safeRead(reader); // login
                        LoginPayload payload = (LoginPayload) req.getPayload();
                        String type = req.getType();
                        String username = payload.getUsername();
                        String password = payload.getPassword();
                        var user = database.getUserByUsername(username);
                        BasicUser currentUser = new BasicUser();

                        if (user == null && type.equals("REGISTRATION")){
                            RegistrationPayload creationDetails = (RegistrationPayload) ServerInterface.safeRead(reader);
                            BasicUser newUser = new BasicUser(creationDetails.getUserName(), creationDetails.getPassword(), creationDetails.getAdmin(), creationDetails.getType());

                            database.addUser(newUser);

                            ServerResponse res = new ServerResponse("accountCreated", new ServerPayload(true, "acccountCreated"));
                            writer.writeObject(res);
                            writer.flush();
                        } else if (user == null && type.equals("LOGIN")  || user != null && type.equals("REGISTRATION")) {
                            ServerResponse res = new ServerResponse("regularMessage", new ServerPayload(false, "failure"));
                            writer.writeObject(res); // client should handle this by saying password or account name wrong
                            writer.flush();
                            continue;
                        } else if (user != null && type.equals("LOGIN")) {
                            // validate login in here some how
                            BasicUser tempUser = new BasicUser("temp", password, false);
                            String hashedPassword = tempUser.getPassword();
                            if (user.getPassword().equals(hashedPassword)) {
                                currentUser = user;
                                ServerResponse res = new ServerResponse("regularMessage", new ServerPayload(true, "login success"));
                                writer.writeObject(res);
                                writer.flush();
                            }
                            else {
                                ServerResponse res = new ServerResponse("regularMessage", new ServerPayload(false, "failure"));
                                writer.writeObject(res); // client should handle this by saying password or account name wrong
                                writer.flush();
                                continue;
                            }
                        }
                        while (true) {
                            var clientRequest = ServerInterface.safeRead(reader);
                            type = clientRequest.getType();
                            if (type.equals("RESERVE")) {
                                ReservationPayload reservationInfo = (ReservationPayload) clientRequest.getPayload();
                                BasicReservation reservation = new BasicReservation(currentUser.getUsername(),
                                        reservationInfo.getMovie(),
                                        reservationInfo.getReservationDate(),
                                        reservationInfo.getStartRow(),
                                        reservationInfo.getStartSeat());
                                boolean reserveStatus = database.reserve(currentUser, reservation);
                                ServerResponse res = new ServerResponse("reserveStatus", new ServerPayload(reserveStatus, "reserveStatus"));
                                writer.writeObject(res);
                                writer.flush();

                            } else if (type.equals("AVAILABILITY")) {
                                List<Auditorium> auditoriums = database.getAuditoriums();
                                List<String> names = new ArrayList<>();
                                for (var auditorium: auditoriums) {
                                    names.add(auditorium.getMovie());
                                }
                                ServerResponse response = new ServerResponse("names", new MovieListPayload(names));
                                writer.writeObject(response);
                                writer.flush();

                                ServerResponse res = new ServerResponse("availability", new AvailabilityPayload(auditoriums));
                                writer.writeObject(res);
                                writer.flush();
                            } else if (type.equals("ISADMIN")) {
                                boolean bool = currentUser.isAdmin();
                                ServerResponse response = new ServerResponse("isAdmin", new ServerPayload(bool, "isAdmin"));
                                writer.writeObject(response);
                                writer.flush();

                            } else if (type.equals("EDITSHOWINGNAME")) {
                                EditShowingNamePayload nameLoad = (EditShowingNamePayload) clientRequest.getPayload();
                                LocalDateTime time = nameLoad.getTime();
                                String name = nameLoad.getNewMovieName();

                                boolean pass = database.editMovieName(time, name);
                                ServerResponse res = new ServerResponse("editName", new ServerPayload(pass, "passfail"));
                                writer.writeObject(res);
                                writer.flush();

                            } else if (type.equals("EDITSHOWINGTIME")) {
                                EditShowingTimePayload timeInfo = (EditShowingTimePayload) clientRequest.getPayload();
                                LocalDateTime oldTime = timeInfo.getOldtime();
                                LocalDateTime newTime = timeInfo.getNewTime();

                                boolean pass = database.editShowingTime(oldTime, newTime);
                                ServerResponse response = new ServerResponse("editShowingTime", new ServerPayload(pass, "passfail"));
                                writer.writeObject(response);
                                writer.flush();

                            } else if (type.equals("CANCELSHOWING")) {
                                CancelShowingPayload load = (CancelShowingPayload) clientRequest.getPayload();
                                LocalDateTime time = load.getTime();
                                boolean pass = database.deleteAuditorium(time);
                                ServerResponse response = new ServerResponse("cancelShowing", new ServerPayload(pass, "passfail"));
                                writer.writeObject(response);
                                writer.flush();

                            } else if (type.equals("CREATEVENUE")) {
                                CreateVenuePayload venue = (CreateVenuePayload) clientRequest.getPayload();
                                String name = venue.getShowingName();
                                int rows = venue.getRows();
                                int cols = venue.getCols();
                                String movieName = venue.getShowingName();
                                LocalDateTime time = venue.getShowingTime();
                                double price = venue.getDefaultSeatPrice();

                                database.createAuditorium(rows, cols, price, movieName, time);
                                ServerResponse response = new ServerResponse("createVenue", new ServerPayload(true, "pass"));
                                writer.writeObject(response);
                                writer.flush();
                            }
                            else if (type.equals("EXIT")) {
                                break;
                            } else {
                                continue;
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Server-Client communication error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server set-up: " + e.getMessage());
        }
    }

    //generates a square auditorium
    public Auditorium genAuditorium(int rows, int cols, double prices, LocalDateTime date) {
        return new Auditorium(rows, cols, prices, date);
    }

}