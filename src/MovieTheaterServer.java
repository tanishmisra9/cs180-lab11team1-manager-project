package src;

import java.io.*;
import java.net.*;

public class MovieTheaterServer implements Runnable, ServerInterface {

    private static final int DEFAULT_PORT = 4242; // what we used in HW11
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ReservationDatabase database; // uses db.dat

    // Custom Port
    public MovieTheaterServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.database = ReservationDatabase.loadDatabase();
        System.out.println("Server started on port " + port);
    }

    // Default Constructor
    public MovieTheaterServer() throws IOException {
        this(DEFAULT_PORT);
    }

    @Override
    public void run() {
        System.out.println("Server is running..");
        System.out.println("Waiting for connections..");

        while (true) {
            try {
                // this is for icoming client connection
                System.out.println("Waiting for client to connect...");
                clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // TODO: Handle client communication, must be Thread-safe


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Starts the Server
    public static void main(String[] args) {
        try {
            MovieTheaterServer server = new MovieTheaterServer();
            Thread serverThread = new Thread(server);
            serverThread.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

}