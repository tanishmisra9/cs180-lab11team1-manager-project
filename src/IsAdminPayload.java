package src;

import java.io.Serial;
import java.io.Serializable;

public class IsAdminPayload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public boolean isAdmin;
    public IsAdminPayload(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    public boolean isAdmin() { return isAdmin; }
}
