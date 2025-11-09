package src.UserClasses;

import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

/*
 * Manages user accounds using thread-safe operations.
 */
class UserAccountManager {

    // Hash map to store users by username
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    // Creates a new user account.
    // Returns true if creation succeeds, false if username already exists.

    public void boot(String filename) {

        // debug 
        System.out.println("[DEBUG] Loading from: " + new File(filename).getAbsolutePath());

        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("An issue occured when retrieving the user database. ");
            return;
        }

        // debug
        System.out.println("[DEBUG] File exists! ");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            
            String line = br.readLine();
            while (line != null) {

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String username = parts[0];
                String password = parts[1];
                boolean isAdmin = Boolean.parseBoolean(parts[2]);
                int typeCode = Integer.parseInt(parts[3]);
                UserType type;

                switch (typeCode) {
                    case 1: type = UserType.PREMIUM;
                        break;
                    case 2: type = UserType.VIP;
                        break;
                    default: type = UserType.REGULAR;
                }

                createUser(username, password, isAdmin, type);

                line = br.readLine();
            }
            System.out.println("Loaded "  + getUserCount() + " users. ");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveToFile(String filename) {

        // debug 
        System.out.println("[DEBUG] Loading from: " + new File(filename).getAbsolutePath());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            
            for (User user : users.values()) {
                if (user instanceof BasicUser b) {
                    
                    int typeCode;
                    switch (b.getType()) {
                        case PREMIUM: typeCode = 1;
                        case VIP: typeCode = 2;
                        default: typeCode = 0;
                    };

                    bw.write(String.format("%s,%s,%b,%d%n",
                            b.getUsername(), b.getPassword(), b.isAdmin(), typeCode));
                }
            }

            System.out.println("Users saved to file.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createUser(String username, String password, boolean isAdmin, UserType type) {
        synchronized (this) {
            if (users.containsKey(username)) {
                return false; // Username already exists
            }
            users.put(username, new BasicUser(username, password, isAdmin, type));
            return true;
        }
    }

    //Logs in a user.
    //Returns true if username and password match, false otherwise.
     
    public boolean login(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    //Deletes an existing user account.
    //Returns true if deletion succeeds, false if username does not exist.
    public boolean deleteUser(String username) {
        synchronized (this) { // Prevent race conditions on deletion
            return users.remove(username) != null;
        }
    }

    //Change password for an existing user.
    //Returns true if successful, false if username/password do not match.
    
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        synchronized (this) { // Ensure atomic password change
            User user = users.get(username);
            if (user != null && user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                return true;
            }
            return false;
        }
    }

    // returns number of users (for testing)
    public int getUserCount() {
        return users.size();
    }
}
