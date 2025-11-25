package src;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Interface for the Movie Theater Server.
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Tanish Misra
 * @version Nov 19, 2025
 */
public interface ServerInterface {

    /**
     * Runs the server and handles client connections
     */
    void run();

    static ClientRequest safeRead(ObjectInputStream ois) {
        try {
            return (ClientRequest) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}