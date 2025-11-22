package src;

import java.net.*;
import java.io.*;


public class Server implements ServerInterface {
    private boolean exit = false;

    public static void main(String[] args) {
        ReservationDataBase database = ReservationDataBase.loadDatabase();
        int expression;
        try (ServerSocket serverSocket = new ServerSocket(4242))  {
            while (!exit) {
                System.out.println("Waiting for client to connect...");

                try (Socket socket = serverSocket.accept();
                     ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream reader = new ObjectInputStream(socket.getInputStream())) {

                    System.out.println("Client connected!");

                    var clientRequest = reader.readObject();
                    //TODO: figure out how to see if account is just created
                    if (account not already created){
                        var creationDetails = reader.readObject();
                        database.addUser(user);
                    }
                    else if (Account already created){
                        var clientRequest = reader.readObject();
                        LoginPayload payload = clientRequest.getPayload();
                        String username = payload.getUsername();
                        String password = payload.getPassword();
                        var user = database.getUser(username);

                        if (user == null) {
                        //TODO: send to client that login failed.

                        continue;
                        } else if (/*USERNAME DOESNT MATCH WITH PASSWORD*/) {
                        //TODO: send to client that login failed.
                        continue;
                       }
                    }
                    // Assuming client sends over a Reservation object
                    var choice = reader.readObject();
                    expression = choice.getchoice();
                    switch (expression) {
                        case 1: //print available seats
//                            System.out.println("");
                            break;
                        case 2: //reservation
                            Reservation reservation = reader.readObject();

                            boolean a = database.reserve(reservation);
                            writer.writeBoolean(a); // client handles if successful or not
                            break;
                        case 3: //

                            break;

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