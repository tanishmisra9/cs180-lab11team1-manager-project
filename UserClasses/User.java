package UserClasses;


public interface User {

    String getUsername();
    String getPassword();
    boolean isAdmin();
    double getPriceMultiplier();

    void setPassword(String newPassword);
    void setAdmin(boolean isAdmin);

}