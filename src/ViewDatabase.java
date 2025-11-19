package src;

import java.util.List;

public class ViewDatabase {
    public static void main(String[] args) {
        System.out.println("Loading database from db.dat...\n");

        ReservationDatabase db = ReservationDatabase.loadDatabase();

        // View Users
        List<BasicUser> users = db.getUsers();
        System.out.println("== USERS ==");
        System.out.println("Total users: " + users.size());
        System.out.println();

        for (int i = 0; i < users.size(); i++) {
            BasicUser u = users.get(i);
            System.out.printf("%d. Username: %s\n", i + 1, u.getUsername());
            System.out.printf("Type: %s\n", u.getType());
            System.out.printf("Admin: %s\n", u.isAdmin());
            System.out.printf("Credit Card: %s\n", u.getCreditCard() != null ? u.getCreditCard().getMaskedNumber() : "None");
            System.out.printf("Reservations: %d\n", u.getReservations().size());
            System.out.printf("Transactions: %d\n", u.getTransactionHistory().size());
            System.out.println();
        }

        // View Auditoriums
        List<Auditorium> auditoriums = db.getAuditoriums();
        System.out.println("== AUDITORIUMS ==");
        System.out.println("Total auditoriums: " + auditoriums.size());
        System.out.println();

        for (int i = 0; i < auditoriums.size(); i++) {
            Auditorium a = auditoriums.get(i);
            System.out.printf("%d. Movie: %s\n", i + 1, a.getShowingName());
            System.out.printf("   Showing Time: %s\n", a.getShowingTime());
            System.out.printf("   Dimensions: %d rows x %d cols\n", a.getRowNumber(), a.getColumnNumber());
            System.out.println();
        }
    }
}