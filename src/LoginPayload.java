package src;   
import java.io.Serializable;

public class LoginPayload implements Serializable {
    private final String username;
    private final String password;
    private final boolean isAdmin;

    public LoginPayload(String user, String pass, boolean isAdmin) {
        this.username = user;
        this.password = pass;
        this.isAdmin = isAdmin;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean getIsAdmin() { return isAdmin; }
}
