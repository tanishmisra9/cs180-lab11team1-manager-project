package src;
import java.io.Serializable;
import java.util.List;

public class AvailabilityPayload implements Serializable {
    private final List<Auditorium> info;

    public AvailabilityPayload(List<Auditorium> info) {
        this.info = info;
    }

    public List<Auditorium> getInfo() { return info; }
}
