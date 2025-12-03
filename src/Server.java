package src;

import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Server implements ServerInterface, Runnable {
    private boolean exit = false;
    private ReservationDatabase database;
    private static BasicUser currentUser;

    public Server() {
        this.database = ReservationDatabase.loadDatabase();
        this.database.populateDefaults();
        System.out.println("Movies in loaded database:");
        for (Auditorium a : database.getAuditoriums()) {
            System.out.println(a.getShowingName() + " @ " + a.getShowingTime());
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(4242)) {
            while (!exit) {  // server loop
                System.out.println("Waiting for client to connect...");

                try (Socket socket = serverSocket.accept();
                     ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream reader = new ObjectInputStream(socket.getInputStream())) {

                    System.out.println("Client connected!");

                    while (true) {  // client loop
                        ClientRequest req = ServerInterface.safeRead(reader);
                        if (req == null) {
                            System.out.println("Client disconnected!");
                            break;
                        }
                        /* ------------------------ REGISTRATION ------------------------ */
                        if (req.getPayload() instanceof RegistrationPayload) {
                            RegistrationPayload p = (RegistrationPayload) req.getPayload();
                            BasicUser newUser = new BasicUser(p.getUsername(), p.getPassword(), p.isAdmin());

                            database.addUser(newUser);

                            writer.writeObject(new ServerResponse(
                                    "accountCreated",
                                    new ServerPayload(true, "accountCreated")));
                            writer.flush();
                        }

                        /* ------------------------ LOGIN ------------------------ */
                        else if (req.getPayload() instanceof LoginPayload) {
                            LoginPayload p = (LoginPayload) req.getPayload();
                            var user = database.getUserByUsername(p.getUsername());

                            BasicUser temp = new BasicUser("temp", p.getPassword(), p.getIsAdmin());
                            String hashed = temp.getPassword();

                            if (user != null && user.getPassword().equals(hashed)) {
                                currentUser = user;

                                writer.writeObject(new ServerResponse(
                                        "regularMessage",
                                        new ServerPayload(true, "login success")));
                                writer.flush();
                                continue;
                            } else {
                                writer.writeObject(new ServerResponse(
                                        "regularMessage",
                                        new ServerPayload(false, "failure")));
                                writer.flush();
                                continue;
                            }
                        }

                        /* ------------------------ RESERVATION ------------------------ */
                        else if (req.getPayload() instanceof ReservationPayload) {
                            ReservationPayload p = (ReservationPayload) req.getPayload();

                            BasicReservation reservation = new BasicReservation(
                                    currentUser.getUsername(),
                                    p.getAuditorium().getMovie(),
                                    p.getAuditorium().getShowingDate(),
                                    p.getStartRow(),
                                    p.getStartSeat()
                            );

                            boolean success = database.reserve(currentUser, reservation);

                            writer.writeObject(new ServerResponse(
                                    "reserveStatus",
                                    new ServerPayload(success, "reserveStatus")));
                            writer.flush();
                        }

                        /* ------------------------ AVAILABILITY ------------------------ */
                        else if (req.getPayload() instanceof AvailabilityRequestPayload) {
                            var auditoriums = database.getAuditoriums();
                            List<String> names = new ArrayList<>();

                            for (Auditorium a : auditoriums) {
                                names.add(a.getMovie());
                            }

                            writer.writeObject(new ServerResponse("names", new MovieListPayload(names)));
                            writer.flush();

                            writer.writeObject(new ServerResponse("availability", new AvailabilityPayload(auditoriums)));
                            writer.flush();
                        }

                        /* ------------------------ IS ADMIN ------------------------ */
                        else if (req.getPayload() instanceof IsAdminPayload) {
                            boolean adminStatus = currentUser.isAdmin();

                            writer.writeObject(new ServerResponse(
                                    "isAdmin",
                                    new ServerPayload(adminStatus, "isAdmin")));
                            writer.flush();
                        }

                        /* ------------------------ EDIT SHOWING NAME ------------------------ */
                        else if (req.getPayload() instanceof EditShowingNamePayload) {
                            EditShowingNamePayload p = (EditShowingNamePayload) req.getPayload();

                            boolean pass = database.editMovieName(p.getTime(), p.getNewMovieName());

                            writer.writeObject(new ServerResponse(
                                    "editName",
                                    new ServerPayload(pass, "passfail")));
                            writer.flush();
                        }

                        /* ------------------------ EDIT SHOWING TIME ------------------------ */
                        else if (req.getPayload() instanceof EditShowingTimePayload) {
                            EditShowingTimePayload p = (EditShowingTimePayload) req.getPayload();

                            boolean pass = database.editShowingTime(p.getOldTime(), p.getNewTime());

                            writer.writeObject(new ServerResponse(
                                    "editShowingTime",
                                    new ServerPayload(pass, "passfail")));
                            writer.flush();
                        }

                        /* ------------------------ CANCEL SHOWING ------------------------ */
                        else if (req.getPayload() instanceof CancelShowingPayload) {
                            CancelShowingPayload p = (CancelShowingPayload) req.getPayload();

                            boolean pass = database.deleteAuditorium(p.getTime());

                            writer.writeObject(new ServerResponse(
                                    "cancelShowing",
                                    new ServerPayload(pass, "passfail")));
                            writer.flush();
                        }

                        /* ------------------------ CREATE VENUE ------------------------ */
                        else if (req.getPayload() instanceof CreateVenuePayload) {
                            CreateVenuePayload p = (CreateVenuePayload) req.getPayload();

                            database.createAuditorium(
                                    p.getRows(),
                                    p.getCols(),
                                    p.getDefaultSeatPrice(),
                                    p.getShowingName(),
                                    p.getShowingTime()
                            );

                            writer.writeObject(new ServerResponse(
                                    "createVenue",
                                    new ServerPayload(true, "pass")));
                            writer.flush();
                        }

                        /* ------------------------ EXIT ------------------------ */
                        else if (req.getPayload() instanceof ExitPayload) {
                            break; // exits client loop
                        }

                        /* ------------------------ UNKNOWN ------------------------ */
                        else {
                            System.out.println("Unknown request payload: " +
                                    req.getPayload().getClass().getSimpleName());
                        }

                    } // end client loop

                } catch (IOException e) {
                    System.out.println("Server-Client communication error: " + e.getMessage());
                }
            } // end server loop
        } catch (IOException e) {
            System.out.println("Server set-up: " + e.getMessage());
        }
    }

    public Auditorium genAuditorium(int rows, int cols, double prices, LocalDateTime date) {
        return new Auditorium(rows, cols, prices, date);
    }
}


// commented code
 
// if (user == null && type.equals("REGISTER")){
// } else if (user == null && type.equals("LOGIN")  || user != null && type.equals("REGISTRATION")) {
//     ServerResponse res = new ServerResponse("regularMessage", new ServerPayload(false, "failure"));
//     writer.writeObject(res); // client should handle this by saying password or account name wrong
//     writer.flush();
//     continue;
// } else if (user != null && type.equals("LOGIN")) {
//     // validate login in here some how
//     BasicUser tempUser = new BasicUser("temp", password, false);
//     String hashedPassword = tempUser.getPassword();
//     if (user.getPassword().equals(hashedPassword)) {
//         currentUser = user;
//         ServerResponse res = new ServerResponse("regularMessage", new ServerPayload(true, "login success"));
//         writer.writeObject(res);
//         writer.flush();
//     }
//     else {
//         ServerResponse res = new ServerResponse("regularMessage", new ServerPayload(false, "failure"));
//         writer.writeObject(res); // client should handle this by saying password or account name wrong
//         writer.flush();
//         continue;
//     }
// }
// while (true) {
//     var clientRequest = ServerInterface.safeRead(reader);
//     type = clientRequest.getType();
//     if (type.equals("RESERVE")) {
//         ReservationPayload reservationInfo = (ReservationPayload) clientRequest.getPayload();
//         BasicReservation reservation = new BasicReservation(currentUser.getUsername(),
//                 reservationInfo.getMovie(),
//                 reservationInfo.getReservationDate(),
//                 reservationInfo.getStartRow(),
//                 reservationInfo.getStartSeat());
//         boolean reserveStatus = database.reserve(currentUser, reservation);
//         ServerResponse res = new ServerResponse("reserveStatus", new ServerPayload(reserveStatus, "reserveStatus"));
//         writer.writeObject(res);
//         writer.flush();

//     } else if (type.equals("AVAILABILITY")) {
//         List<Auditorium> auditoriums = database.getAuditoriums();
//         List<String> names = new ArrayList<>();
//         for (var auditorium: auditoriums) {
//             names.add(auditorium.getMovie());
//         }
//         ServerResponse response = new ServerResponse("names", new MovieListPayload(names));
//         writer.writeObject(response);
//         writer.flush();

//         ServerResponse res = new ServerResponse("availability", new AvailabilityPayload(auditoriums));
//         writer.writeObject(res);
//         writer.flush();
//     } else if (type.equals("ISADMIN")) {
//         boolean bool = currentUser.isAdmin();
//         ServerResponse response = new ServerResponse("isAdmin", new ServerPayload(bool, "isAdmin"));
//         writer.writeObject(response);
//         writer.flush();
//     } else if (type.equals("EDITSHOWINGTIME")) {
//         EditShowingTimePayload timeInfo = (EditShowingTimePayload) clientRequest.getPayload();
//         LocalDateTime oldTime = timeInfo.getOldTime();
//         LocalDateTime newTime = timeInfo.getNewTime();

//         boolean pass = database.editShowingTime(oldTime, newTime);

//     } else if (type.equals("EDITSHOWINGNAME")) {
//         EditShowingNamePayload nameLoad = (EditShowingNamePayload) clientRequest.getPayload();
//         LocalDateTime time = nameLoad.getTime();
//         String name = nameLoad.getNewMovieName();

//         boolean pass = database.editMovieName(time, name);
//         ServerResponse res = new ServerResponse("editName", new ServerPayload(pass, "passfail"));
//         writer.writeObject(res);
//         writer.flush();

//     } else if (type.equals("EDITSHOWINGTIME")) {
//         EditShowingTimePayload timeInfo = (EditShowingTimePayload) clientRequest.getPayload();
//         LocalDateTime oldTime = timeInfo.getOldTime();
//         LocalDateTime newTime = timeInfo.getNewTime();

//         boolean pass = database.editShowingTime(oldTime, newTime);
//         ServerResponse response = new ServerResponse("editShowingTime", new ServerPayload(pass, "passfail"));
//         writer.writeObject(response);
//         writer.flush();

//     } else if (type.equals("CANCELSHOWING")) {
//         CancelShowingPayload load = (CancelShowingPayload) clientRequest.getPayload();
//         LocalDateTime time = load.getTime();
//         boolean pass = database.deleteAuditorium(time);
//         ServerResponse response = new ServerResponse("cancelShowing", new ServerPayload(pass, "passfail"));
//         writer.writeObject(response);
//         writer.flush();

//     } else if (type.equals("CREATEVENUE")) {
//         CreateVenuePayload venue = (CreateVenuePayload) clientRequest.getPayload();

//         String name = venue.getShowingName();
//         int rows = venue.getRows();
//         int cols = venue.getCols();
//         String movieName = venue.getShowingName();
//         LocalDateTime time = venue.getShowingTime();
//         double price = venue.getDefaultSeatPrice();

//         database.createAuditorium(rows, cols, price, movieName, time);
//         ServerResponse response = new ServerResponse("createVenue", new ServerPayload(true, "pass"));
//         writer.writeObject(response);
//         writer.flush();
//     }
//     else if (type.equals("EXIT")) {
//         break;
//     } else {
//         continue;
//     }
// }
// }
