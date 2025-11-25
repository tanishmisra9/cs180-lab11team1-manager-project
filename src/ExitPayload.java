package src;

import java.io.Serial;
import java.io.Serializable;

public class ExitPayload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private boolean exit;
    public ExitPayload() {
        exit = true;
    }

}
