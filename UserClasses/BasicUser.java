package UserClasses;

/**
 * Represents a user account.
 */
public class BasicUser implements User {

    private String username;
    private String password;
    private boolean isAdmin;
    private UserType type;

    public BasicUser(String username, String password, boolean isAdmin, UserType type) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.type = type;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAdmin() {
        return isAdmin;
    }
   
    @Override
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    @Override
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("User[%s, admin=%s]", username, isAdmin);
    }

}