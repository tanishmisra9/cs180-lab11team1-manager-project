package src;
import java.io.Serializable;

public class ServerPayload implements Serializable {
    private final List<Auditorium> info;

    public ServerPayload(List<Auditorium> info) {
        this.info = info;
        }

    public List<Auditorium> getInfo() { return info; }
}
