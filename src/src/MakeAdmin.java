package src;

public class MakeAdmin {
    public static void main(String[] args) {
        ReservationDatabase db = ReservationDatabase.loadDatabase();

        String username = "tanishmisra";
        BasicUser user = db.getUserByUsername(username);
        if (user != null) {
            user.setAdmin(true);
            db.saveData();
            System.out.println("tanishmisra is now an admin!");
        } else {
            System.out.println("User not found.");
        }
    }
}