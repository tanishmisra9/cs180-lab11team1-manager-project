package src;

import java.time.format.DateTimeFormatter;
import java.util.*;
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
                       // isAdmin = payload.getSuccess();
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

                System.out.println("registering");
                service.register(username, password);
                System.out.println("request sent to server");
                ServerResponse resp = service.receiveResponse();
                System.out.println("response recieved");

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
        service.isAdmin();
        ServerResponse response = service.receiveResponse();
        //System.out.println("response recieved!!!!!!!!!");
        if (response != null && response.getPayload() instanceof ServerPayload payload) {
            isAdmin = payload.getSuccess();
          //  System.out.println("\n welcome admin");
        }

        // fetch from server once per session.

        service.requestMovies();
        ServerResponse resp = service.receiveResponse();

        List<String> movies = Arrays.asList("Madagascar", "Godzilla", "Cars", "Toy Story"); // fallback
        if (resp != null && resp.getPayload() instanceof MovieListPayload mlp) {
            movies = mlp.getNames();
        }
        resp = service.receiveResponse();
        AvailabilityPayload load = (AvailabilityPayload) resp.getPayload();
        List<Auditorium> auditoriumList = load.getInfo();
        // USER FLOW
        userFlow:
        while (!isAdmin) {

            Set<String> uniqueMovies =new TreeSet<>(movies);

            System.out.println("\nAvailable showings:");
            uniqueMovies.forEach(System.out::println);

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

            // Seat selection, we cant just get one auditorium for a movie.
            List<Auditorium> showingsForMovie = new ArrayList<>();
            int n = 0;
           // service.requestAuditorium(movieSelected[0]);
            // resp = service.receiveResponse();
            for (Auditorium a: auditoriumList) {
                if (a.getMovie().equals(movieSelected[0])) {
                    showingsForMovie.add(a);
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a");
                    String pretty = a.getShowingTime().format(fmt);
                    System.out.println(n + ": " + pretty);
                    n++;
                }
            }
            System.out.println("Choose movie showing time");
            int dateChoice = sc.nextInt();
            sc.nextLine();
            Auditorium chosenAuditorium = showingsForMovie.get(dateChoice);


            /*if (resp != null && resp.getPayload() instanceof AuditoriumPayload ap) {
                seating = ap.getSeats();
            }*/

                while (true) {
                    System.out.println("\nSeating (_=empty, X=taken):");
                    String[][] seating = chosenAuditorium.getSeats();
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

                    int r = -1, c = -1, userRow = -1, userCol = -1;
                    try {
                        userRow = Integer.parseInt(rowStr);
                        userCol = Integer.parseInt(colStr);
                        r = userRow - 1;
                        c = userCol - 1;
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

                service.reserveSeat(userRow, userCol, currentUsername, chosenAuditorium);
                resp = service.receiveResponse();
                if (resp != null && resp.getPayload() instanceof ServerPayload payload) {
                    ServerPayload tuna = (ServerPayload) resp.getPayload();
                    if (tuna.getSuccess()) {
                        chosenAuditorium.setReservation(currentUsername, r, c);
                        System.out.println("Reservation successful!");
                    } else {
                        System.out.println("Reservation failed.");
                    }

                }
                break;
            }
            System.out.println("Make another reservation? Y N");
            String answer = sc.nextLine();
            if (answer.equalsIgnoreCase("n")) break;

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
                        System.out.println("Enter movie to reserve seat in:"); // we cant distinguish by movie, it has to be by time.
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

                       // service.reserveSeat(row, col, userToReserve, TODO: Auditorium here, look above, time implementation like that just put it here. );
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
                                System.out.println("Enter old time:");
                                System.out.println("Enter new year:");
                                int year = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new month:");
                                int month = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new day:");
                                int day = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new hour:");
                                int hour = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter new minute:");
                                int min = Integer.parseInt(sc.nextLine());
                                LocalDateTime changeTime = LocalDateTime.of(year, month, day, hour, min);

                                service.editShowingName(movieToEdit, newName, changeTime);
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
