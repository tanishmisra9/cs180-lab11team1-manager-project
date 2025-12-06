package src;

import java.io.Serializable;

public class AvailabilityRequestPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String type;

    public AvailabilityRequestPayload(String type) {
        this.type = type;
    }

    public String getType() { return type;}
}
