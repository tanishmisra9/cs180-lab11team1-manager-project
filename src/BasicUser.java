package src;
//committed properly
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Objects;

public class BasicUser implements User, Serializable {

    private static final long serialVersionUID = 1L;

    private final String username;
    private String password;
    private boolean isAdmin;
    private UserType type;
    private transient CreditCard creditCard;

    private final List<BasicReservation> reservations = new CopyOnWriteArrayList<>();
    private final List<String> transactionHistory = new CopyOnWriteArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    // Constructors
    public BasicUser(String username, String password, boolean isAdmin, UserType type) {
        this(username, password, isAdmin, type, false);
    }

    public BasicUser(String username, String password, boolean isAdmin, UserType type, boolean isHashed) {
        this.username = username;
        this.password = isHashed ? password : hashPassword(password);
        this.isAdmin = isAdmin;
        this.type = type;
    }

    public BasicUser(String username, String password, boolean isAdmin) {
        this(username, password, isAdmin, UserType.REGULAR);
    }

    public BasicUser() {
        this("default", "password", false);
    }

    @Override
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return password; }

    @Override
    public boolean isAdmin() { return isAdmin; }

    @Override
    public double getPriceMultiplier() {
        return switch (type) {
            case PREMIUM -> 0.9;
            case VIP -> 0.75;
            default -> 1.0;
        };
    }

    @Override
    public void setPassword(String newPassword) {
        lock.lock();
        try {
            this.password = hashPassword(newPassword);
        } finally { lock.unlock(); }
    }

    @Override
    public void setAdmin(boolean isAdmin) {
        lock.lock();
        try { this.isAdmin = isAdmin; } finally { lock.unlock(); }
    }

    public UserType getType() { lock.lock(); try { return type; } finally { lock.unlock(); } }

    public CreditCard getCreditCard() { lock.lock(); try { return creditCard; } finally { lock.unlock(); } }

    public void setCreditCard(CreditCard card) { lock.lock(); try { this.creditCard = card; } finally { lock.unlock(); } }

    public List<BasicReservation> getReservations() { return List.copyOf(reservations); }

    public List<String> getTransactionHistory() { return List.copyOf(transactionHistory); }

    public void addTransaction(String transaction) { transactionHistory.add(transaction); }

    public boolean upgradeUser(UserType newType, double cost) {
        lock.lock();
        try {
            if (creditCard == null) {
                System.out.println("Upgrade failed: No credit card on file.");
                return false;
            }
            if (newType.ordinal() <= this.type.ordinal()) {
                System.out.println("Upgrade failed: Cannot downgrade or re-purchase same tier.");
                return false;
            }
            String transaction = String.format(
                    "Charged $%.2f to %s for upgrade to %s",
                    cost, creditCard.getMaskedNumber(), newType
            );
            transactionHistory.add(transaction);
            this.type = newType;
            System.out.println("Upgrade successful: " + transaction);
            return true;
        } finally { lock.unlock(); }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "User[%s, type=%s, admin=%s, card=%s]",
                username, type, isAdmin,
                (creditCard != null ? creditCard.getMaskedNumber() : "None")
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicUser u)) return false;
        return getUsername().equals(u.getUsername());
    }

    @Override
    public int hashCode() { return Objects.hash(getUsername()); }

    public void addReservation(String movie, LocalDateTime dateTime, int row, int seat, int people, double seatPrice) {
        BasicReservation reservation = new BasicReservation(this.getUsername(),
                movie,
                dateTime,
                row,
                seat);
        reservations.add(reservation);
    }
}
