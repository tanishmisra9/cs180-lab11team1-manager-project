package src;
import java.io.Serializable;

public class ServerPayload implements Serializable {
    private final boolean success;
    private final String message;

    public ServerPayload(boolean suc, String mes) {
        success = suc;
        message = mes;
    }

    public String getSuccess() { return success; }
    public String getMessage() { return message; }
}
