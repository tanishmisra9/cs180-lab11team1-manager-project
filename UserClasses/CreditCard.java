package UserClasses;

public class CreditCard {
    private final String cardHolderName;
    private final String cardNumber; // Ideally encrypted or tokenized
    private final String expiryDate;
    private final String cvv;

    public CreditCard(String cardHolderName, String cardNumber, String expiryDate, String cvv) {
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    public String getMaskedNumber() {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    @Override
    public String toString() {
        return String.format("Card[%s, exp=%s]", getMaskedNumber(), expiryDate);
    }
}
