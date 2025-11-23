package src;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserAccountManager {

    private final ConcurrentHashMap<String, BasicUser> users = new ConcurrentHashMap<>();

    public void boot(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("User database file not found: " + filename);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int loaded = 0;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String username = parts[0];
                String password = parts[1];
                boolean isAdmin = Boolean.parseBoolean(parts[2]);
                int typeCode = Integer.parseInt(parts[3]);

                UserType type;
                switch (typeCode) {
                    case 1 -> type = UserType.PREMIUM;
                    case 2 -> type = UserType.VIP;
                    default -> type = UserType.REGULAR;
                }

                BasicUser user = new BasicUser(username, password, isAdmin, type, true);
                users.put(username, user);
                loaded++;
            }

            System.out.printf("Loaded %d user(s) from %s.%n", loaded, filename);
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    public void saveToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (User user : users.values()) {
                if (user instanceof BasicUser b) {
                    int typeCode = switch (b.getType()) {
                        case VIP -> 2;
                        case PREMIUM -> 1;
                        default -> 0;
                    };
                    bw.write(String.format("%s,%s,%b,%d%n",
                            b.getUsername(),
                            b.getPassword(),
                            b.isAdmin(),
                            typeCode));
                }
            }
            System.out.println("User data saved successfully to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public synchronized boolean createUser(String username, String password, boolean isAdmin, UserType type) {
        if (users.containsKey(username)) {
            System.out.println("Create failed: Username already exists -> " + username);
            return false;
        }

        BasicUser user = new BasicUser(username, password, isAdmin, type);
        users.put(username, user);

        System.out.println("Created user: " + username + " (" + type + ")");
        return true;
    }

    public synchronized boolean deleteUser(String username) {
        boolean removed = users.remove(username) != null;
        if (removed) System.out.println("Deleted user: " + username);
        else System.out.println("Delete failed: User not found -> " + username);
        return removed;
    }

    public synchronized boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = users.get(username);
        if (user == null) {
            System.out.println("Change password failed: User not found -> " + username);
            return false;
        }
        if (!user.getPassword().equals(oldPassword)) {
            System.out.println("Change password failed: Incorrect current password for " + username);
            return false;
        }
        user.setPassword(newPassword);
        System.out.println("Password updated for user: " + username);
        return true;
    }

    public synchronized boolean upgradeUser(String username, UserType newType, double cost) {
        BasicUser user = users.get(username);
        if (user == null) {
            System.out.println("Upgrade failed: User not found -> " + username);
            return false;
        }
        return user.upgradeUser(newType, cost);
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful: " + username);
            return true;
        }
        System.out.println("Login failed: Invalid credentials for " + username);
        return false;
    }

    public int getUserCount() {
        return users.size();
    }
}
