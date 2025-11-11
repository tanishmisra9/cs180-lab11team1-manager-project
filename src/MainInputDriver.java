package src;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import src.Auditorium;
import src.BasicReservation;
import src.User;
import src.UserType;

/**
 * A small interactive CLI to exercise the manager and reservation system.
 * Supports: register, login, add auditorium (admin), make reservation, view reservations, save & exit.
 */
public class MainInputDriver {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        UserAccountManager userAccounts = new UserAccountManager();
        // Try loading Data/users.txt first, fallback to users.txt
        userAccounts.boot("Data/users.txt");
        if (userAccounts.getUserCount() == 0) userAccounts.boot("users.txt");

        ReservationDatabase reservations = ReservationDatabase.loadDatabase();

        String currentUser = null;

        outer: while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1) Register");
            System.out.println("2) Login");
            System.out.println("3) Logout");
            System.out.println("4) Add Auditorium (admin only)");
            System.out.println("5) Make Reservation");
            System.out.println("6) View My Reservations");
            System.out.println("7) Save & Exit");
            System.out.print("Choose an option: ");

            String choice = in.nextLine().trim();
            switch (choice) {
                case "1" -> { // Register
                    System.out.print("Username: ");
                    String username = in.nextLine().trim();
                    System.out.print("Password: ");
                    String password = in.nextLine().trim();
                    System.out.print("Type (0=REGULAR,1=PREMIUM,2=VIP): ");
                    String t = in.nextLine().trim();
                    UserType type;
                    try {
                        int code = Integer.parseInt(t);
                        type = switch (code) {
                            case 1 -> UserType.PREMIUM;
                            case 2 -> UserType.VIP;
                            default -> UserType.REGULAR;
                        };
                    } catch (NumberFormatException e) {
                        type = UserType.REGULAR;
                    }
                    userAccounts.createUser(username, password, false, type);
                }

                case "2" -> { // Login
                    System.out.print("Username: ");
                    String username = in.nextLine().trim();
                    System.out.print("Password: ");
                    String password = in.nextLine().trim();
                    if (userAccounts.login(username, password)) {
                        currentUser = username;
                    }
                }

                case "3" -> { // Logout
                    currentUser = null;
                    System.out.println("Logged out.");
                }

                case "4" -> { // Add Auditorium
                    if (currentUser == null) {
                        System.out.println("Must be logged in as admin to add auditoriums.");
                        break;
                    }
                    User u = userAccounts.getUser(currentUser);
                    if (u == null || !u.isAdmin()) {
                        System.out.println("Admin privileges required.");
                        break;
                    }

                    try {
                        System.out.print("Rows: ");
                        int rows = Integer.parseInt(in.nextLine().trim());
                        System.out.print("Cols: ");
                        int cols = Integer.parseInt(in.nextLine().trim());
                        System.out.print("Price per seat (e.g. 10.00): ");
                        double price = Double.parseDouble(in.nextLine().trim());
                        System.out.print("Movie name: ");
                        String movie = in.nextLine().trim();
                        System.out.print("Showing date-time (YYYY-MM-DDTHH:MM): ");
                        String dt = in.nextLine().trim();
                        LocalDateTime showTime = LocalDateTime.parse(dt);

                        Auditorium a = new Auditorium(rows, cols, price, movie, showTime);
                        reservations.addAuditorium(a);
                        System.out.println("Auditorium added: " + movie + " @ " + showTime);
                    } catch (NumberFormatException | DateTimeParseException ex) {
                        System.out.println("Invalid input: " + ex.getMessage());
                    }
                }

                case "5" -> { // Make Reservation
                    if (currentUser == null) {
                        System.out.println("Please login before making a reservation.");
                        break;
                    }
                    User u = userAccounts.getUser(currentUser);
                    if (u == null) {
                        System.out.println("Current user not found.");
                        break;
                    }

                    try {
                        System.out.print("Movie name: ");
                        String movie = in.nextLine().trim();
                        System.out.print("Showing date-time (YYYY-MM-DDTHH:MM): ");
                        LocalDateTime showTime = LocalDateTime.parse(in.nextLine().trim());
                        System.out.print("Row (1-based): ");
                        int row = Integer.parseInt(in.nextLine().trim());
                        System.out.print("Starting seat (1-based): ");
                        int seat = Integer.parseInt(in.nextLine().trim());
                        System.out.print("Number of people: ");
                        int num = Integer.parseInt(in.nextLine().trim());

                        int success = 0;
                        for (int i = 0; i < num; i++) {
                            BasicReservation r = new BasicReservation(u.getUsername(), movie, showTime, row, seat + i);
                            boolean ok = reservations.reserve(u, r);
                            if (ok) success++;
                        }
                        System.out.println("Reserved " + success + " / " + num + " seats.");
                    } catch (Exception ex) {
                        System.out.println("Error making reservation: " + ex.getMessage());
                    }
                }

                case "6" -> { // View My Reservations
                    if (currentUser == null) {
                        System.out.println("Please login to view reservations.");
                        break;
                    }
                    User u = userAccounts.getUser(currentUser);
                    if (u == null) {
                        System.out.println("User not found.");
                        break;
                    }
                    var list = reservations.getReservations(u);
                    if (list.isEmpty()) {
                        System.out.println("No reservations found.");
                    } else {
                        System.out.println("Reservations for " + currentUser + ":");
                        for (var r : list) {
                            System.out.println(" - Movie: " + r.getMovie() + " @ " + r.getDateTime() + " seat: " + r.getRow() + "," + r.getSeat());
                        }
                    }
                }

                case "7" -> { // Save & Exit
                    System.out.println("Saving data...");
                    reservations.saveData();
                    userAccounts.saveToFile("Data/users.txt");
                    System.out.println("Goodbye.");
                    break outer;
                }

                default -> System.out.println("Unknown option: " + choice);
            }
        }

        in.close();
    }
}
