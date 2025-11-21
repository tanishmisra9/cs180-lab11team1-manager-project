package src;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Objects;

/**
 * Basic implementation of a reservation.
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Ved Joshi
 * @version Nov 19, 2025
 */
public class BasicReservation implements Reservation, Serializable {
    private static final long serialVersionUID = 1L;

    // fields
    private String user;
    private String movie;
    private LocalDateTime date;
    private int row;
    private int seat;
    private boolean active = true;
    private int numPeople;

    // constructor
    public BasicReservation(String user, String movie, LocalDateTime date, int row, int seat) {

        // check for empty strings
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("User can't be empty.");
        }
        if (movie == null || movie.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie can't be empty.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Reservation date can't be null.");
        } //else if (date ) TODO: add prevent if date is in past can't add rez

        // check for illegal nums
        if (row < 1 || seat < 1) {
            throw new IllegalArgumentException("Row and seat numbers must be positive. ");
        }

        this.user = user;
        this.movie = movie;
        this.date = date;
        this.row = row;
        this.seat = seat;
        numPeople = 1;
    }

    public BasicReservation(String user, String movie, LocalDateTime date, int row, int seat, int people) {

        // check for empty strings
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("User can't be empty.");
        }
        if (movie == null || movie.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie can't be empty.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Reservation date can't be null.");
        }

        // check for illegal nums
        if (row < 1 || seat < 1) {
            throw new IllegalArgumentException("Row and seat numbers must be positive.");
        }

        this.user = user;
        this.movie = movie;
        this.date = date;
        this.row = row;
        this.seat = seat;
        numPeople = people;
    }

    // Identifying and linking
    @Override
    public String getUser() {
        return user; // who booked the reservation
    }

    @Override
    public String getShowtime() {
        return "";
    }

    // Movie and scheduling
    @Override
    public String getMovie() {
        return movie;
    }

    @Override
    public LocalDate getDate() {
        return null;
    }

    @Override
    public LocalDateTime getDateTime() {
        return date;
    }

    // Seating info
    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getSeat() {
        return seat;
    }

    // Status and actions
    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void cancel() {
        active = false;
    }

    public int getPeople() {
        return numPeople;
    }

    // Overrides .equals() to check if two people are trying to reserve the same time, seat, and movie
    @Override
    public boolean equals(Object o) {
        if (this == o) { //quick check for equality
            return true;
        } else if (o == null) {
            return false;
        }

        BasicReservation other = (BasicReservation) o; // casting object to use getters

        if (movie.equals(other.getMovie()) &&
                date.equals(other.getDateTime()) &&
                row == other.getRow() &&
                seat == other.getSeat()) {
            return true;
        } else {
            return false;
        }
    }

    // Need to override hashcode if I override .equals() so that only equivalent objects have same hash
    @Override
    public int hashCode() {
        return Objects.hash(movie, date, row, seat);
    }
}