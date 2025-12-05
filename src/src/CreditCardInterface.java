package src;

/**
 * Interface defining operations for credit card information.
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Tanish Misra
 * @version Nov 19, 2025
 */
public interface CreditCardInterface {

    /**
     * Gets the cardholder's name.
     * @return the name on the card
     */
    String getCardHolderName();

    /**
     * Gets the expiration date.
     * @return expiration date in MM/YY format
     */
    String getExpiryDate();

    /**
     * Gets a masked version of the card number for security.
     * @return masked card number (e.g., "**** **** **** 1234")
     */
    String getMaskedNumber();
}