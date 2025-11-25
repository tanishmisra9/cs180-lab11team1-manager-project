package src;

import java.net.*;
import java.io.*;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
=======
import java.time.LocalDateTime;
>>>>>>> 2164a3dfb3175427a1a0737e4ac3ac249d07b130


public class Server implements ServerInterface {
    private boolean exit = false;

    public static ClientRequest safeRead(ObjectInputStream ois) {
        try {
            return (ClientRequest) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        ReservationDatabase database = ReservationDatabase.loadDatabase();
        int expression;
        try (ServerSocket serverSocket = new ServerSocket(4242))  {
            while (!exit) { //server loop
                System.out.println("Waiting for client to connect...");

                try (Socket socket = serverSocket.accept();
                     ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream reader = new ObjectInputStream(socket.getInputStream())) {

                    System.out.println("Client connected!");

                    while (true) { // individual client loop
                        ClientRequest req = safeRead(reader); // login
                        LoginPayload payload = (LoginPayload) req.getPayload();
                        String type = req.getType();
                        String username = payload.getUsername();
                        String password = payload.getPassword();
                        var user = database.getUserByUsername(username);
                        BasicUser currentUser;

                        if (user == null && !type.equals("LOGIN")){
                            var creationDetails = safeRead(reader);
                            database.addUser(user);
                        } else if (user == null && type.equals("LOGIN")  || user != null && !type.equals("LOGIN")) {
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
                            var clientRequest = safeRead(reader);
                            type = clientRequest.getType();
                            if (type.equals("RESERVE")) {
                                ReservationPayload reservationInfo = clientRequest.getPayload();
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
                            } else if (type.equals("EXIT")) {
                                break;
                            } else {
                                continue;
                            }
                        }
            }




                    /*
                    String user = reservation.getUser();
                    String showtime = reservation.getShowtime(); // showtime ID or descriptor

                    // Movie and scheduling
                    String movie = reservation.getMovie();
                    LocalDate date = reservation.getDate();
                    LocalDateTime localDateTime = reservation.getDateTime();

                    // Seating info
                    int row = reservation.getRow();
                    int seat = reservation.getSeat();

                     */




                } catch (IOException except) {
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