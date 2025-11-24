package src;

import java.net.*;
import java.io.*;


public class Server implements ServerInterface {
    private boolean exit = false;

    public static void main(String[] args) {
        ReservationDataBase database = ReservationDataBase.loadDatabase();
        int expression;
        try (ServerSocket serverSocket = new ServerSocket(4242))  {
            while (!exit) { //server loop
                System.out.println("Waiting for client to connect...");

                try (Socket socket = serverSocket.accept();
                     ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream reader = new ObjectInputStream(socket.getInputStream())) {

                    System.out.println("Client connected!");

                    while (true) { // individual client loop
                        var clientRequest = reader.readObject(); // login
                        LoginPayload payload = clientRequest.getPayload();
                        String type = clientRequest.getType();
                        String username = payload.getUsername();
                        String password = payload.getPassword();
                        var user = database.getUser(username);
                        BasicUser currentUser;

                        if (user == null && !type.equals("LOGIN")){
                            var creationDetails = reader.readObject();
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
                            if (user.getPassword().equals(hashedPassword)()) {
                                currentUser = user;
                                ServerResponse res = new ServerResponse("regularMessage", new ServerPayload(true, "login success"));
                                writer.writerObject(res);
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
                            var clientRequest = reader.readObject();
                            String type = clientRequest.getType();
                            if (type.equals("RESERVE")) {
                                ReservationPayload = clientRequest.getPayload();
                                BasicReservation reservation = new BasicReservation(currentUser.getUsername(),
                                        ReservationPayload.getMovie(),
                                        ReservationPayload.getDate(),
                                        ReservationPayload.getStartRow(),
                                        ReservationPayload.getStartSeat());
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
                                
                                ServerResponse res = new ServerResponse("availability", new AvailabilityPayload(availability));
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

}