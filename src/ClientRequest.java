package src;

import java.io.Serializable;
import java.util.UUID;

public class ClientRequest implements Serializable {

    private final String id = UUID.randomUUID().toString();
    private final String type;      // e.g., "LOGIN", "SEND_MESSAGE"
    private final Object payload;

    public ClientRequest(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public Object getPayload() { return payload; }
}
