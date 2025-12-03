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
                System.out.println("\n Is Admin? true/false:");
                String adminInput = sc.nextLine();
                boolean isAdminInput = Boolean.parseBoolean(adminInput);
                service.login(username, password, isAdminInput);
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
                System.out.println("\n Is Admin? true/false:");
                String adminInput = sc.nextLine();
                boolean isAdminInput = Boolean.parseBoolean(adminInput);

                System.out.println("registering");
                service.register(username, password, isAdminInput);
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
                if (movies.stream().anyMatch(m -> m.equals(input))) {
                    movieSelected[0] = input;
                    break;
                } // i made it case sensitive because it breaks later if not
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
        // after login / register and isAdmin check
        if (isAdmin) {
            AdminInterface.runAdminFlow(service, sc);
        }

        

        System.out.println("\nThank you for using the service!");
        sc.close();
    }
}
