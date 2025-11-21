package src;

/**
 * User interface defining core user account operations.
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Tanish Misra
 * @version Nov 19, 2025
 */

public interface User {

    /**
     * Gets the username.
     * @return the username
     */
    String getUsername();

    /**
     * Gets the hashed password.
     * @return the hashed password
     */
    String getPassword();

    /**
     * Checks if user has admin privileges.
     * @return true if admin, false otherwise
     */
    boolean isAdmin();

    /**
     * Gets the price multiplier based on user tier.
     * REGULAR: 1.0 (no discount)
     * PREMIUM: 0.9 (10% off)
     * VIP: 0.75 (25% off)
     * @return the price multiplier
     */
    double getPriceMultiplier();

    /**
     * Sets a new password (will be hashed).
     * @param newPassword the new password
     */
    void setPassword(String newPassword);

    /**
     * Sets admin status.
     * @param isAdmin true to grant admin privileges
     */
    void setAdmin(boolean isAdmin);
}