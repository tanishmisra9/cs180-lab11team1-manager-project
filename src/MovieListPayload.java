package src;
import java.io.Serializable;

public class MovieListPayload implements Serializable {
    private final List<String> names;

    public ServerPayload(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() { return names; }
}
