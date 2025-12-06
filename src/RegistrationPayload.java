package src;

import java.io.Serializable;

public class RegistrationPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final boolean isAdmin; // Optional: can default to false for regular users

    public RegistrationPayload(String username, String password) {
        this(username, password, false);
    }

    public RegistrationPayload(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return "RegistrationPayload{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
