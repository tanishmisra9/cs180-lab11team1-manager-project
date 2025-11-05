package UserClasses;

import java.util.concurrent.ConcurrentHashMap;
// manages user accounds using thread safe operations
class UserAccountManager {
    // hash map to store users by username
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    //Creates a new user account.
    //Returns true if creation succeeds, false if username already exists.
    
    public boolean createUser(String username, String password, boolean isAdmin) {
        synchronized (this) { // Ensure no race condition on user creation
            if (users.containsKey(username)) {
                return false; // username already exists
            }
            users.put(username, new User(username, password, isAdmin));
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
