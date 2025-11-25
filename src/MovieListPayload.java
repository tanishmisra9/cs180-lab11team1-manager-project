package src;
import java.io.Serializable;
import java.util.List;

public class MovieListPayload implements Serializable {
    private final List<String> names;

    public MovieListPayload(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() { return names; }
}
