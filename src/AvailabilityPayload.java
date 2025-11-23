package src;
import java.io.Serializable;

public class ServerPayload implements Serializable {
    private final List<String> info;

    public ServerPayload(List<String> info) {
        this.info = info;
        }

    public List<String> getInfo() { return info; }
}
