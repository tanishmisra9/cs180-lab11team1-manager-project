package src;    
import java.io.Serializable;

public class ServerResponse implements Serializable {

    private final String type;
    private final Object payload;

    public ServerResponse(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() { return type; }
    public Object getPayload() { return payload; }
}
