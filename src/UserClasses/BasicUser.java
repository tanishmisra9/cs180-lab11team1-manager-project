package src.UserClasses;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import ReservationClasses.BasicReservation;
import ReservationClasses.Reservation;

public class BasicUser implements User {

    private final String username;
    private String password;
    private boolean isAdmin;
    private UserType type;
    private CreditCard creditCard;

    private final List<Reservation> reservations = new CopyOnWriteArrayList<>();
    private final List<String> transactionHistory = new CopyOnWriteArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public BasicUser(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = hashPassword(password);
        this.isAdmin = isAdmin;
        this.type = UserType.REGULAR;
    }

    // Utility: SHA-256 password hashing
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
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setAdmin(boolean isAdmin) {
        lock.lock();
        try {
            this.isAdmin = isAdmin;
        } finally {
            lock.unlock();
        }
    }

    public UserType getType() {
        lock.lock();
        try {
            return type;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Makes a transaction and reserves seats for 'numPeople' individuals.
     * Each reservation is for one person.
     */
    public boolean addReservation(String movie, String showTime, LocalDate date, int startRow, int startSeat, int numPeople, double reservationFee) {
        lock.lock();
        try {
            if (creditCard == null) {
                System.out.println("Reservation failed: No credit card on file.");
                return false;
            }
            if (numPeople <= 0) {
                System.out.println("Reservation failed: Must reserve for at least one person.");
                return false;
            }

            double totalCost = reservationFee * numPeople * getPriceMultiplier();
            String chargeMessage = String.format(
                "Charged $%.2f to %s for %d-person reservation (movie: %s)",
                totalCost, creditCard.getMaskedNumber(), numPeople, movie
            );

            // Simulated payment
            transactionHistory.add(chargeMessage);

            // Create one reservation per person
            List<Reservation> newReservations = new ArrayList<>();
            for (int i = 0; i < numPeople; i++) {
                int row = startRow;
                int seat = startSeat + i; // seats next to each other
                Reservation r = new BasicReservation(username, showTime, movie, date, row, seat);
                newReservations.add(r);
            }

            reservations.addAll(newReservations);
            System.out.println("Reservation made for " + numPeople + " people: " + movie + " on " + date);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Cancels and refunds reservations for a given movie/show/date for a number of people.
     */
    public boolean cancelReservation(String movie, String showTime, LocalDate date, int numPeople, double reservationFee) {
        lock.lock();
        try {
            int canceled = 0;
            List<Reservation> toCancel = new ArrayList<>();

            for (Reservation r : reservations) {
                if (r.isActive() &&
                    r.getMovie().equals(movie) &&
                    r.getShowtime().equals(showTime) &&
                    r.getDate().equals(date)) {
                    toCancel.add(r);
                    if (++canceled == numPeople) break;
                }
            }

            if (toCancel.isEmpty()) {
                System.out.println("No matching reservations found to cancel.");
                return false;
            }

            double refundAmount = reservationFee * toCancel.size() * getPriceMultiplier();
            for (Reservation r : toCancel) r.cancel();
            reservations.removeAll(toCancel);

            String refundMessage = String.format(
                "Refunded $%.2f to %s for cancelling %d-person reservation (movie: %s)",
                refundAmount, creditCard.getMaskedNumber(), toCancel.size(), movie
            );
            transactionHistory.add(refundMessage);
            System.out.println("Cancelled " + toCancel.size() + " reservations for " + movie);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public List<Reservation> getReservations() {
        return List.copyOf(reservations);
    }

    public void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    public List<String> getTransactionHistory() {
        return List.copyOf(transactionHistory);
    }
    public CreditCard getCreditCard() {
        lock.lock();
        try {
            return creditCard;
        } finally {
            lock.unlock();
        }
    }
    public void setCreditCard(CreditCard card) {
        lock.lock();
        try {
            this.creditCard = card;
        } finally {
            lock.unlock();
        }
    }

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
        } finally {
            lock.unlock();
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
}
