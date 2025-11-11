package src;

/*
 * Public interface for User, allows for specialized Users
 * Defines all operations and fields a user class must support
 */

public interface User {

    String getUsername();
    String getPassword();
    boolean isAdmin();
    double getPriceMultiplier();

    void setPassword(String newPassword);
    void setAdmin(boolean isAdmin);

}
