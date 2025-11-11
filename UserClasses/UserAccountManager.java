package UserClasses;

import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

public class UserAccountManager {

    private final ConcurrentHashMap<String, BasicUser> users = new ConcurrentHashMap<>();

    public void boot(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("User database file not found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String username = parts[0];
                String password = parts[1];
                boolean isAdmin = Boolean.parseBoolean(parts[2]);
                int typeCode = Integer.parseInt(parts[3]);

                UserType type = switch (typeCode) {
                    case 1 -> UserType.PREMIUM;
                    case 2 -> UserType.VIP;
                    default -> UserType.REGULAR;
                };

                BasicUser user = new BasicUser(username, password, isAdmin);
                user.upgradeUser(type, 0.0); // Set proper tier
                users.put(username, user);
            }
            System.out.println("Loaded " + getUserCount() + " users.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (User user : users.values()) {
                if (user instanceof BasicUser b) {
                    int typeCode = switch (b.getType()) {
                        case PREMIUM -> 1;
                        case VIP -> 2;
                        default -> 0;
                    };
                    bw.write(String.format("%s,%s,%b,%d%n",
                            b.getUsername(), b.getPassword(), b.isAdmin(), typeCode));
                }
            }
            System.out.println("Users saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean createUser(String username, String password, boolean isAdmin, UserType type) {
        if (users.containsKey(username)) return false;
        BasicUser user = new BasicUser(username, password, isAdmin);
        user.upgradeUser(type, 0.0);
        users.put(username, user);
        return true;
    }

    public synchronized boolean deleteUser(String username) {
        return users.remove(username) != null;
    }

    public synchronized boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }

    public synchronized boolean upgradeUser(String username, UserType newType, double cost) {
        BasicUser user = users.get(username);
        if (user == null) return false;
        if (newType == UserType.PREMIUM){
            cost = 30.0;
        }
        else if (newType == UserType.VIP){
            cost = 50.0;
        }
        return user.upgradeUser(newType, cost);
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    public int getUserCount() { return users.size(); }
}
