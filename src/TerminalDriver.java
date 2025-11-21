package src;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Terminal-based driver for the Movie Theater Reservation System.
 * Uses ReservationDatabase (db.dat) as the primary data store.
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Tanish Misra
 * @version Nov 19, 2025
 */
public class TerminalDriver {

    private static ReservationDatabase db;
    private static Scanner scanner;
    private static BasicUser currentUser;

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        initializeSystem();

        System.out.println("== WELCOME TO THE MOVIE BOOKING SYSTEM! ==");

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = handleGuestMenu();
            } else {
                running = handleUserMenu();
            }
        }

        shutdown();
    }

    /**
     * Initializes the system by loading the reservation database.
     */
    private static void initializeSystem() {
        System.out.println("Loading database...");
        db = ReservationDatabase.loadDatabase();
        System.out.println("System initialized successfully.\n");
    }

    /**
     * Handles the guest menu for non-logged-in users.
     */
    private static boolean handleGuestMenu() {
        System.out.println("\n--- GUEST MENU ---");
        System.out.println("1. Register New Account");
        System.out.println("2. Login");
        System.out.println("3. Browse Available Movies");
        System.out.println("4. Exit");
        System.out.print("Select an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                registerUser();
                break;
            case "2":
                login();
                break;
            case "3":
                browseMovies();
                break;
            case "4":
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
        }

        return true;
    }

    /**
     * Handles the main menu for logged-in users.
     */
    private static boolean handleUserMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("Logged in as: " + currentUser.getUsername() +
                " (" + currentUser.getType() + ")");

        if (currentUser.getCreditCard() != null) {
            System.out.println("Credit Card: " + currentUser.getCreditCard().getMaskedNumber());
        } else {
            System.out.println("Warning: No credit card on file");
        }

        System.out.println("\n1. Browse Movies & Showtimes");
        System.out.println("2. Make a Reservation");
        System.out.println("3. View My Reservations");
        System.out.println("4. Cancel a Reservation");
        System.out.println("5. Manage Credit Card");
        System.out.println("6. View Transaction History");
        System.out.println("7. Upgrade Account");
        System.out.println("8. Change Password");

        if (currentUser.isAdmin()) {
            System.out.println("\n--- ADMIN OPTIONS ---");
            System.out.println("9. Add New Movie Showing");
            System.out.println("10. View Auditorium Details");
            System.out.println("11. View Seat Map");
        }

        System.out.println("\n0. Logout");
        System.out.print("Select an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                browseMovies();
                break;
            case "2":
                makeReservation();
                break;
            case "3":
                viewMyReservations();
                break;
            case "4":
                cancelReservation();
                break;
            case "5":
                manageCreditCard();
                break;
            case "6":
                viewTransactionHistory();
                break;
            case "7":
                upgradeAccount();
                break;
            case "8":
                changePassword();
                break;
            case "9":
                if (currentUser.isAdmin()) {
                    addMovieShowing();
                } else {
                    System.out.println("Admin access required.");
                }
                break;
            case "10":
                if (currentUser.isAdmin()) {
                    viewAuditoriumDetails();
                } else {
                    System.out.println("Admin access required.");
                }
                break;
            case "11":
                if (currentUser.isAdmin()) {
                    viewSeatMap();
                } else {
                    System.out.println("Admin access required.");
                }
                break;
            case "0":
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }

        return true;
    }

    /**
     * Registers a new user account and adds to database.
     */
    private static void registerUser() {
        System.out.println("\n--- REGISTER NEW ACCOUNT ---");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        // Check if username already exists
        if (db.getUserByUsername(username) != null) {
            System.out.println("Username already exists. Please choose another.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        BasicUser newUser = new BasicUser(username, password, false, UserType.REGULAR);
        db.addUser(newUser);

        System.out.println("\nAccount created successfully!");
        System.out.println("You can now log in with your credentials.");
    }

    /**
     * Handles user login by searching the database.
     */
    private static void login() {
        System.out.println("\n--- LOGIN ---");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        BasicUser user = db.getUserByUsername(username);

        if (user == null) {
            System.out.println("\nLogin failed. User not found.");
            return;
        }

        // Hash the password and compare
        BasicUser tempUser = new BasicUser("temp", password, false);
        String hashedPassword = tempUser.getPassword();

        if (user.getPassword().equals(hashedPassword)) {
            currentUser = user;
            System.out.println("\nLogin successful! Welcome, " + username + "!");
        } else {
            System.out.println("\nLogin failed. Invalid password.");
        }
    }

    /**
     * Logs out the current user.
     */
    private static void logout() {
        if (currentUser != null) {
            System.out.println("\nLogging out " + currentUser.getUsername() + "...");
            currentUser = null;
            System.out.println("Logged out successfully.");
        }
    }

    /**
     * Browses available movies and showtimes from database.
     */
    private static void browseMovies() {
        System.out.println("\n--- AVAILABLE MOVIES & SHOWTIMES ---");

        List<Auditorium> auditoriums = db.getAuditoriums();

        if (auditoriums.isEmpty()) {
            System.out.println("No movies currently available.");
            System.out.println("Please check back later or contact an administrator.");
            return;
        }

        System.out.println("\nTotal showings: " + auditoriums.size());
        System.out.println("---------------------------------------------------");

        for (int i = 0; i < auditoriums.size(); i++) {
            Auditorium a = auditoriums.get(i);
            System.out.printf("%d. %s\n", i + 1, a.getShowingName());
            System.out.printf("   Date/Time: %s\n", a.getShowingTime().format(DATE_TIME_FORMAT));
            System.out.printf("   Auditorium: %d rows x %d columns\n", a.getRowNumber(), a.getColumnNumber());

            // Count available seats
            int availableSeats = 0;
            String[][] seats = a.getSeats();
            for (int r = 0; r < seats.length; r++) {
                for (int c = 0; c < seats[r].length; c++) {
                    if (seats[r][c].equals("empty")) {
                        availableSeats++;
                    }
                }
            }
            System.out.printf("   Available seats: %d\n", availableSeats);
            System.out.println("---------------------------------------------------");
        }
    }

    /**
     * Makes a new reservation through the database.
     */
    private static void makeReservation() {
        if (currentUser.getCreditCard() == null) {
            System.out.println("\nYou need to add a credit card before making reservations.");
            System.out.print("Would you like to add one now? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                manageCreditCard();
                if (currentUser.getCreditCard() == null) {
                    return;
                }
            } else {
                return;
            }
        }

        System.out.println("\n--- MAKE A RESERVATION ---");

        System.out.print("Enter movie name: ");
        String movie = scanner.nextLine().trim();

        System.out.print("Enter showing date and time (yyyy-MM-dd HH:mm): ");
        String dateTimeStr = scanner.nextLine().trim();

        LocalDateTime showTime;
        try {
            showTime = LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm");
            return;
        }

        System.out.print("Enter row number (1-based): ");
        int row;
        try {
            row = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid row number.");
            return;
        }

        System.out.print("Enter starting seat number (1-based): ");
        int seat;
        try {
            seat = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid seat number.");
            return;
        }

        System.out.print("Number of seats to reserve: ");
        int numSeats;
        try {
            numSeats = Integer.parseInt(scanner.nextLine().trim());
            if (numSeats < 1) {
                System.out.println("Must reserve at least one seat.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }

        // Calculate discount and total
        double discount = (1 - currentUser.getPriceMultiplier()) * 100;

        System.out.printf("\nYour %s discount: %.0f%%\n", currentUser.getType(), discount);
        System.out.print("\nConfirm reservation? (y/n): ");

        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Reservation cancelled.");
            return;
        }

        // Try to make reservations through the database
        int successCount = 0;
        for (int i = 0; i < numSeats; i++) {
            BasicReservation reservation = new BasicReservation(
                    currentUser.getUsername(),
                    movie,
                    showTime,
                    row,
                    seat + i,
                    1
            );

            if (db.reserve(currentUser, reservation)) {
                successCount++;
            } else {
                System.out.printf("Warning: Seat %d in row %d is not available\n", seat + i, row);
            }
        }

        if (successCount > 0) {
            System.out.printf("\nSuccessfully reserved %d out of %d seat(s)!\n",
                    successCount, numSeats);
            System.out.println("Total charged: Check transaction history for details.");
        } else {
            System.out.println("\nReservation failed. Seats may not be available or showing not found.");
        }
    }

    /**
     * Views the current user's reservations from database.
     */
    private static void viewMyReservations() {
        System.out.println("\n--- MY RESERVATIONS ---");

        List<BasicReservation> reservations = db.getReservations(currentUser);

        if (reservations == null || reservations.isEmpty()) {
            System.out.println("You have no reservations.");
            return;
        }

        System.out.println("\nTotal reservations: " + reservations.size());
        System.out.println("---------------------------------------------------");

        for (int i = 0; i < reservations.size(); i++) {
            BasicReservation r = reservations.get(i);
            System.out.printf("%d. Movie: %s\n", i + 1, r.getMovie());
            System.out.printf("   Date/Time: %s\n", r.getDateTime().format(DATE_TIME_FORMAT));
            System.out.printf("   Seat: Row %d, Seat %d\n", r.getRow(), r.getSeat());
            System.out.printf("   Status: %s\n", r.isActive() ? "Active" : "Cancelled");
            System.out.println("---------------------------------------------------");
        }
    }

    /**
     * Cancels a reservation.
     */
    private static void cancelReservation() {
        System.out.println("\n--- CANCEL RESERVATION ---");

        List<BasicReservation> reservations = db.getReservations(currentUser);

        if (reservations == null || reservations.isEmpty()) {
            System.out.println("You have no reservations to cancel.");
            return;
        }

        // Show active reservations
        List<BasicReservation> activeReservations = new ArrayList<>();
        for (BasicReservation r : reservations) {
            if (r.isActive()) {
                activeReservations.add(r);
            }
        }

        if (activeReservations.isEmpty()) {
            System.out.println("You have no active reservations to cancel.");
            return;
        }

        System.out.println("\nActive Reservations:");
        for (int i = 0; i < activeReservations.size(); i++) {
            BasicReservation r = activeReservations.get(i);
            System.out.printf("%d. %s - %s - Row %d, Seat %d\n",
                    i + 1, r.getMovie(),
                    r.getDateTime().format(DATE_TIME_FORMAT),
                    r.getRow(), r.getSeat());
        }

        System.out.print("\nSelect reservation to cancel (number): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > activeReservations.size()) {
                System.out.println("Invalid selection.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        BasicReservation toCancel = activeReservations.get(choice - 1);

        System.out.printf("\nCancel reservation for %s on %s? (y/n): ",
                toCancel.getMovie(), toCancel.getDateTime().format(DATE_TIME_FORMAT));

        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Cancellation aborted.");
            return;
        }

        toCancel.cancel();
        System.out.println("\nReservation cancelled successfully!");
        System.out.println("Note: Refund processing depends on cancellation policy.");
    }

    /**
     * Manages credit card information.
     */
    private static void manageCreditCard() {
        System.out.println("\n--- MANAGE CREDIT CARD ---");

        if (currentUser.getCreditCard() != null) {
            System.out.println("Current card: " + currentUser.getCreditCard());
            System.out.print("Would you like to update your card? (y/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                return;
            }
        }

        System.out.print("Cardholder name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Card number (16 digits): ");
        String number = scanner.nextLine().trim();

        System.out.print("Expiry date (MM/YY): ");
        String expiry = scanner.nextLine().trim();

        System.out.print("CVV: ");
        String cvv = scanner.nextLine().trim();

        CreditCard card = new CreditCard(name, number, expiry, cvv);
        currentUser.setCreditCard(card);

        System.out.println("\nCredit card added successfully!");
        System.out.println("Card: " + card.getMaskedNumber());
    }

    /**
     * Views transaction history.
     */
    private static void viewTransactionHistory() {
        System.out.println("\n--- TRANSACTION HISTORY ---");

        List<String> history = currentUser.getTransactionHistory();

        if (history.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\nTotal transactions: " + history.size());
        System.out.println("---------------------------------------------------");

        for (int i = 0; i < history.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, history.get(i));
        }

        System.out.println("---------------------------------------------------");
    }

    /**
     * Upgrades user account tier.
     */
    private static void upgradeAccount() {
        System.out.println("\n--- UPGRADE ACCOUNT ---");
        System.out.println("Current tier: " + currentUser.getType());
        System.out.println("\nAvailable tiers:");
        System.out.println("1. PREMIUM - 10% discount on all tickets ($30.00)");
        System.out.println("2. VIP - 25% discount on all tickets ($50.00)");
        System.out.print("\nSelect tier (1-2, 0 to cancel): ");

        String choice = scanner.nextLine().trim();

        UserType newType;
        double cost;

        switch (choice) {
            case "1":
                newType = UserType.PREMIUM;
                cost = 30.00;
                break;
            case "2":
                newType = UserType.VIP;
                cost = 50.00;
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid option.");
                return;
        }

        if (currentUser.getCreditCard() == null) {
            System.out.println("\nYou need a credit card to upgrade.");
            return;
        }

        System.out.printf("\nUpgrade to %s for $%.2f? (y/n): ", newType, cost);
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Upgrade cancelled.");
            return;
        }

        boolean success = currentUser.upgradeUser(newType, cost);

        if (success) {
            System.out.println("\nAccount upgraded successfully!");
            System.out.printf("You now have a %.0f%% discount on all tickets!\n",
                    (1 - currentUser.getPriceMultiplier()) * 100);
        }
    }

    /**
     * Changes user password.
     */
    private static void changePassword() {
        System.out.println("\n--- CHANGE PASSWORD ---");

        System.out.print("Enter current password: ");
        String oldPassword = scanner.nextLine().trim();

        // Create temp user to hash the old password
        BasicUser tempUser = new BasicUser("temp", oldPassword, false);
        String hashedOld = tempUser.getPassword();

        if (!currentUser.getPassword().equals(hashedOld)) {
            System.out.println("\nIncorrect current password.");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();

        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("\nPasswords do not match.");
            return;
        }

        currentUser.setPassword(newPassword);
        System.out.println("\nPassword changed successfully!");
    }

    /**
     * Admin function to add a new movie showing to database.
     */
    private static void addMovieShowing() {
        System.out.println("\n--- ADD NEW MOVIE SHOWING ---");

        System.out.print("Movie name: ");
        String movie = scanner.nextLine().trim();

        System.out.print("Showing date and time (yyyy-MM-dd HH:mm): ");
        String dateTimeStr = scanner.nextLine().trim();

        LocalDateTime showTime;
        try {
            showTime = LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
            return;
        }

        System.out.print("Number of rows: ");
        int rows;
        try {
            rows = Integer.parseInt(scanner.nextLine().trim());
            if (rows < 1) {
                System.out.println("Must have at least 1 row.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }

        System.out.print("Number of columns: ");
        int cols;
        try {
            cols = Integer.parseInt(scanner.nextLine().trim());
            if (cols < 1) {
                System.out.println("Must have at least 1 column.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }

        System.out.print("Price per seat: $");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine().trim());
            if (price < 0) {
                System.out.println("Price cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid price.");
            return;
        }

        Auditorium auditorium = new Auditorium(rows, cols, price, movie, showTime);
        db.addAuditorium(auditorium);

        System.out.println("\nMovie showing added successfully to database!");
        System.out.printf("   %s - %s\n", movie, showTime.format(DATE_TIME_FORMAT));
        System.out.printf("   Auditorium: %d rows x %d columns @ $%.2f per seat\n", rows, cols, price);
    }

    /**
     * Admin function to view auditorium details.
     */
    private static void viewAuditoriumDetails() {
        System.out.println("\n--- AUDITORIUM DETAILS ---");

        List<Auditorium> auditoriums = db.getAuditoriums();

        if (auditoriums.isEmpty()) {
            System.out.println("No auditoriums in database.");
            return;
        }

        System.out.println("\nTotal auditoriums: " + auditoriums.size());
        System.out.println("---------------------------------------------------");

        for (int i = 0; i < auditoriums.size(); i++) {
            Auditorium a = auditoriums.get(i);
            System.out.printf("%d. %s\n", i + 1, a.getShowingName());
            System.out.printf("   Date/Time: %s\n", a.getShowingTime().format(DATE_TIME_FORMAT));
            System.out.printf("   Dimensions: %d rows x %d columns\n", a.getRowNumber(), a.getColumnNumber());

            // Count seats
            int total = 0;
            int reserved = 0;
            String[][] seats = a.getSeats();
            for (int r = 0; r < seats.length; r++) {
                for (int c = 0; c < seats[r].length; c++) {
                    total++;
                    if (!seats[r][c].equals("empty")) {
                        reserved++;
                    }
                }
            }
            System.out.printf("   Seats: %d reserved / %d total\n", reserved, total);
            System.out.println("---------------------------------------------------");
        }
    }

    /**
     * Admin function to view seat map of an auditorium.
     */
    private static void viewSeatMap() {
        System.out.println("\n--- SEAT MAP VIEWER ---");

        List<Auditorium> auditoriums = db.getAuditoriums();

        if (auditoriums.isEmpty()) {
            System.out.println("No auditoriums in database.");
            return;
        }

        System.out.println("\nSelect auditorium:");
        for (int i = 0; i < auditoriums.size(); i++) {
            Auditorium a = auditoriums.get(i);
            System.out.printf("%d. %s - %s\n", i + 1, a.getShowingName(),
                    a.getShowingTime().format(DATE_TIME_FORMAT));
        }

        System.out.print("Enter number: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > auditoriums.size()) {
                System.out.println("Invalid selection.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        Auditorium a = auditoriums.get(choice - 1);
        String[][] seats = a.getSeats();

        System.out.println("\n--- SEAT MAP: " + a.getShowingName() + " ---");
        System.out.println("Legend: [empty] = Available, [username] = Reserved\n");

        for (int r = 0; r < seats.length; r++) {
            System.out.printf("Row %2d: ", r + 1);
            for (int c = 0; c < seats[r].length; c++) {
                if (seats[r][c].equals("empty")) {
                    System.out.print("[ ] ");
                } else {
                    System.out.printf("[X] ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Shuts down the system and saves database.
     */
    private static void shutdown() {
        System.out.println("\n===================================================");
        System.out.println("Saving database to db.dat...");

        db.saveData();

        System.out.println("All data saved successfully.");
        System.out.println("\nThank you for using the Movie Theater Booking System!");
        System.out.println("Goodbye!");
        System.out.println("===================================================");

        scanner.close();
    }
}