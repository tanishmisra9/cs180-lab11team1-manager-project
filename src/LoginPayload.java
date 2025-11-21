package src;   
import java.io.Serializable;

public class LoginPayload implements Serializable {
    private final String username;
    private final String password;

    public LoginPayload(String user, String pass) {
        this.username = user;
        this.password = pass;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
