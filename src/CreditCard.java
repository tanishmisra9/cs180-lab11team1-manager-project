package src;

import java.awt.*;
import java.io.Serializable;

/**
 * Represents a user's credit card used for transactions.
 */
// pushed changes that were taking forever
public class CreditCard {

    private final String cardHolderName;
    private final String cardNumber;  // Ideally encrypted or tokenized
    private final String expiryDate;
    private final String cvv;

    /**
     * Creates a new CreditCard object.
     *
     * @param cardHolderName the name printed on the card
     * @param cardNumber     the 16-digit card number (unencrypted for this simulation)
     * @param expiryDate     expiration date in MM/YY format
     * @param cvv            3- or 4-digit security code
     */
    public CreditCard(String cardHolderName, String cardNumber, String expiryDate, String cvv) {
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    /**
     * Returns the cardholder's name.
     */
    public String getCardHolderName() {
        return cardHolderName;
    }

    /**
     * Returns the card's expiration date (MM/YY).
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Returns a masked version of the card number.
     * Example: **** **** **** 1234
     */
    public String getMaskedNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "**** **** **** ????";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    /**
     * Returns a human-readable summary of the card (masked).
     */
    @Override
    public String toString() {
        return String.format("Card[%s, exp=%s]", getMaskedNumber(), expiryDate);
    }
}
