package src;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
// committed properly this time
public class AdminInterface {

    public static void runAdminFlow(ClientService service, Scanner sc) {

        System.out.println("Welcome, Admin!");

        adminLoop:
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1: Edit showing");
            System.out.println("2: Create venue");
            System.out.println("Type 'exit' to quit");

            String choice = sc.nextLine();
            switch (choice) {
                case "1": // Edit showing
                    service.requestMovies();
                    ServerResponse resp = service.receiveResponse();

                    List<String> movies = Arrays.asList("Madagascar", "Godzilla", "Cars", "Toy Story"); // fallback
                    if (resp != null && resp.getPayload() instanceof MovieListPayload mlp) {
                        movies = mlp.getNames();
                    }
                    resp = service.receiveResponse();
                    AvailabilityPayload load = (AvailabilityPayload) resp.getPayload();
                    List<Auditorium> auditoriumList = load.getInfo();

                    Set<String> uniqueMovies =new TreeSet<>(movies);

                    System.out.println("\nAvailable movies:");
                    uniqueMovies.forEach(System.out::println);

                    // Movie selection
                    final String[] movieSelected = new String[1]; // mutable holder
                    
                    System.out.println("\nEnter movie to edit:");
                    String movieToEdit = sc.nextLine();

                    System.out.println("\nAvailable showings");
                    List<Auditorium> showingsForMovie = new ArrayList<>();
                    int n = 0;
                // service.requestAuditorium(movieSelected[0]);
                    // resp = service.receiveResponse();
                    for (Auditorium a: auditoriumList) {
                        if (a.getMovie().equals(movieToEdit)) {
                            showingsForMovie.add(a);
                            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a");
                            String pretty = a.getShowingTime().format(fmt);
                            System.out.println("- " + pretty);
                            n++;
                        }
                    }

                    System.out.println("1: Change name\n2: Change time\n3: Cancel showing");
                    String editChoice = sc.nextLine();

                    switch (editChoice) {
                        case "1": // Change name
                            System.out.println("Enter new name:");
                            String newName = sc.nextLine();

                            System.out.println("Enter showing time to identify showing:");
                            LocalDateTime oldTime = parseDateTime(sc);

                            service.editShowingName(movieToEdit, newName, oldTime);
                            break;

                        case "2": // Change time
                            System.out.println("Enter new time:");
                            LocalDateTime newTime = parseDateTime(sc);

                            System.out.println("Enter old time:");
                            LocalDateTime oldTimeForEdit = parseDateTime(sc);

                            service.editShowingTime(movieToEdit, newTime, oldTimeForEdit);
                            break;

                        case "3": // Cancel showing
                            System.out.println("Enter showing time to cancel:");
                            LocalDateTime cancelTime = parseDateTime(sc);

                            service.cancelShowing(movieToEdit, cancelTime);
                            break;

                        default:
                            System.out.println("Invalid choice");
                            break;
                    }
                    break;

                case "2": // Create venue
                    System.out.println("Enter venue name:");
                    String venueName = sc.nextLine();
                    System.out.println("Enter number of rows:");
                    int rows = Integer.parseInt(sc.nextLine());
                    System.out.println("Enter number of cols:");
                    int cols = Integer.parseInt(sc.nextLine());
                    System.out.println("Enter showing name:");
                    String showingName = sc.nextLine();
                    System.out.println("Enter showing time (yyyy-MM-ddTHH:mm):");
                    LocalDateTime showingTime = LocalDateTime.parse(sc.nextLine());
                    System.out.println("Enter default seat price:");
                    double price = Double.parseDouble(sc.nextLine());

                    service.createVenue(venueName, rows, cols, showingName, showingTime, price);
                    ServerResponse createResp = service.receiveResponse();
                    if (createResp != null && createResp.getPayload() instanceof ServerPayload payload) {
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

        System.out.println("Exiting Admin interface. Goodbye!");
    }

    // --------------------------
    // HELPER: Parse LocalDateTime from scanner
    // --------------------------
    private static LocalDateTime parseDateTime(Scanner sc) {
        System.out.println("Enter year:");
        int year = Integer.parseInt(sc.nextLine());
        System.out.println("Enter month:");
        int month = Integer.parseInt(sc.nextLine());
        System.out.println("Enter day:");
        int day = Integer.parseInt(sc.nextLine());
        System.out.println("Enter hour:");
        int hour = Integer.parseInt(sc.nextLine());
        System.out.println("Enter minute:");
        int min = Integer.parseInt(sc.nextLine());
        return LocalDateTime.of(year, month, day, hour, min);
    }
}
