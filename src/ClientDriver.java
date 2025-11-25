package src;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.time.*;

public class ClientDriver {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        BasicClient client = new BasicClient();
        ClientService service = new ClientService(client);
        service.connectToServer();

        boolean isAdmin = false;
        String currentUsername = null;

        System.out.println("Welcome to the ticketing client!");

        // LOGIN / REGISTER
        loginRegisterLoop:
        while (true) {
            System.out.println("\n1: Login\n2: Register\nEnter choice:");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                System.out.println("\nUsername:");
                String username = sc.nextLine();
                System.out.println("\nPassword:");
                String password = sc.nextLine();

                service.login(username, password);
                ServerResponse resp = service.receiveResponse();

                if (resp != null && resp.getPayload() instanceof ServerPayload payload) {
                    if (payload.getSuccess()) {
                        System.out.println("\nLogin successful!");
                        isAdmin = payload.isAdmin();
                        currentUsername = username;
                        break loginRegisterLoop;
                    } else {
                        System.out.println("\nLogin failed: " + payload.getMessage());
                    }
                } else {
                    System.out.println("\nServer did not respond.");
                }

            } else if (choice.equals("2")) {
                System.out.println("\nEnter desired username:");
                String username = sc.nextLine();
                System.out.println("\nEnter password:");
                String password = sc.nextLine();

                service.register(username, password);
                ServerResponse resp = service.receiveResponse();

                if (resp != null && resp.getPayload() instanceof ServerPayload payload) {
                    if (payload.getSuccess()) {
                        System.out.println("\nRegistration successful! Please log in.");
                    } else {
                        System.out.println("\nRegistration failed: " + payload.getMessage());
                    }
                } else {
                    System.out.println("\nServer did not respond.");
                }

            } else {
                System.out.println("Invalid input.");
            }
        }

        // USER FLOW
        userFlow:
        while (!isAdmin) {

            service.requestMovies();
            ServerResponse resp = service.receiveResponse();
            List<String> movies = Arrays.asList("Madagascar", "Godzilla", "Cars", "Toy Story"); // fallback
            if (resp != null && resp.getPayload() instanceof MovieListPayload mlp) {
                movies = mlp.getNames();
            }

            System.out.println("\nAvailable showings:");
            movies.forEach(System.out::println);

            // Movie selection
            final String[] movieSelected = new String[1]; // mutable holder

            while (true) {
                System.out.println("\nSelect movie:");
                String input = sc.nextLine();
                if (movies.stream().anyMatch(m -> m.equalsIgnoreCase(input))) {
                    movieSelected[0] = input; // set selected movie
                    break;
                }
                System.out.println("Invalid movie!");
            }

            // Seat selection
            service.requestAuditorium(movieSelected[0]);
            resp = service.receiveResponse();
            String[][] seating = new String[0][];
            if (resp != null && resp.getPayload() instanceof AuditoriumPayload ap) {
                seating = ap.getSeats();
            }

            while (true) {
                System.out.println("\nSeating (_=empty, X=taken):");
                for (String[] row : seating) {
                    for (String seat : row) {
                        System.out.print((seat.equals("empty") ? "_" : "X") + " ");
                    }
                    System.out.println();
                }

                System.out.println("Enter row (or 'exit'):");
                String rowStr = sc.nextLine();
                if (rowStr.equalsIgnoreCase("exit")) break userFlow;

                System.out.println("Enter col:");
                String colStr = sc.nextLine();

                int r = -1, c = -1;
                try {
                    r = Integer.parseInt(rowStr) - 1;
                    c = Integer.parseInt(colStr) - 1;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid numbers.");
                    continue;
                }

                if (r < 0 || r >= seating.length || c < 0 || c >= seating[r].length) {
                    System.out.println("Out of bounds.");
                    continue;
                }
                if (!seating[r][c].equals("empty")) {
                    System.out.println("Seat taken.");
                    continue;
                }

                service.reserveSeat(movieSelected[0], r, c, currentUsername);
                resp = service.receiveResponse();
                if (resp != null && resp.getPayload() instanceof ServerPayload payload) {
                    System.out.println(payload.getMessage());
                }
                break;
            }
        }

        // ADMIN FLOW
        if (isAdmin) {
            adminLoop:
            while (true) {
                System.out.println("\n--- Admin Menu ---");
                System.out.println("1: Reserve seat for user");
                System.out.println("2: Edit showing");
                System.out.println("3: Create venue");
                System.out.println("Type 'exit' to quit");

                String input = sc.nextLine();
                switch (input) {
                    case "1":
                        System.out.println("Enter movie to reserve seat in:");
                        String movie = sc.nextLine();
                        service.requestAuditorium(movie);
                        ServerResponse seatResp = service.receiveResponse();
                        String[][] seats = new String[0][];
                        if (seatResp != null && seatResp.getPayload() instanceof AuditoriumPayload ap) {
                            seats = ap.getSeats();
                        }

                        System.out.println("Enter username for reservation:");
                        String userToReserve = sc.nextLine();

                        System.out.println("Enter row:");
                        int row = Integer.parseInt(sc.nextLine()) - 1;
                        System.out.println("Enter col:");
                        int col = Integer.parseInt(sc.nextLine()) - 1;

                        service.reserveSeat(movie, row, col, userToReserve);
                        ServerResponse res = service.receiveResponse();
                        if (res != null && res.getPayload() instanceof ServerPayload payload) {
                            System.out.println(payload.getMessage());
                        }
                        break;

                    case "2":
                        System.out.println("Enter movie to edit:");
                        String movieToEdit = sc.nextLine();
                        System.out.println("1: Change name\n2: Change time\n3: Cancel showing");
                        String editChoice = sc.nextLine();

                        switch (editChoice) {
                            case "1":
                                System.out.println("Enter new name:");
                                String newName = sc.nextLine();
                                service.editShowingName(movieToEdit, newName);
                                break;
                            case "2":
                                System.out.println("Enter new year:");
                                int y = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new month:");
                                int mo = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new day:");
                                int d = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new hour:");
                                int h = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new minute:");
                                int mi = Integer.parseInt(sc.nextLine());
                                LocalDateTime newTime = LocalDateTime.of(y, mo, d, h, mi);

                                System.out.println("Enter new year:");
                                y = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new month:");
                                mo = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new day:");
                                d = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new hour:");
                                h = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new minute:");
                                mi = Integer.parseInt(sc.nextLine());
                                LocalDateTime oldTime = LocalDateTime.of(y, mo, d, h, mi);
                                service.editShowingTime(movieToEdit, newTime, oldTime);
                                break;
                            case "3":
                                service.cancelShowing(movieToEdit);
                                break;
                            default:
                                System.out.println("Invalid choice");
                                break;
                        }
                        break;

                    case "3":
                        System.out.println("Enter venue name:");
                        String venueName = sc.nextLine();
                        System.out.println("Enter rows:");
                        int venueRows = Integer.parseInt(sc.nextLine());
                        System.out.println("Enter cols:");
                        int venueCols = Integer.parseInt(sc.nextLine());
                        System.out.println("Enter showing name:");
                        String showingName = sc.nextLine();
                        System.out.println("Enter time (yyyy-mm-ddTHH:mm):");
                        LocalDateTime showingTime = LocalDateTime.parse(sc.nextLine());
                        System.out.println("Enter default seat price:");
                        double price = Double.parseDouble(sc.nextLine());

                        service.createVenue(venueName, venueRows, venueCols, showingName, showingTime, price);
                        ServerResponse venueResp = service.receiveResponse();
                        if (venueResp != null && venueResp.getPayload() instanceof ServerPayload payload) {
                            System.out.println(payload.getMessage());
                        }
                        break;

                    case "exit":
                        break adminLoop;
                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }
        }

        System.out.println("\nThank you for using the service!");
        sc.close();
    }
}