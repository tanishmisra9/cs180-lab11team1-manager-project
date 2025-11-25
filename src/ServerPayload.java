package src;
import java.io.Serializable;

public class ServerPayload implements Serializable {
    private final boolean success;
    private final String message;

    public ServerPayload(boolean suc, String mes) {
        success = suc;
        message = mes;
    }

    public boolean getSuccess() { return success; }
    public String getMessage() { return message; }

   /* public boolean isAdmin() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAdmin'");
    }*/ // dont need this method IMO just use getSuccess, it contains a bool anyways. - sebastian
}
