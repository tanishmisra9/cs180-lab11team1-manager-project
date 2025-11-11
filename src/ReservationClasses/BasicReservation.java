package src.ReservationClasses;


import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Objects;

public class BasicReservation implements Reservation, Serializable {
    //fields
    private String user;
    private String movie;
    private LocalDateTime date;
    private int row;
    private int seat;
    private boolean active = true;

    //constructor
    public BasicReservation(String user, String movie, LocalDateTime date, int row, int seat) {

        //check for empty strings
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("User can't be empty.");
        }
        if (movie == null || movie.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie can't be empty.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Reservation date can't be null.");
        }

        //check for illegal nums
        if (row < 1 || seat < 1) {
            throw new IllegalArgumentException("Row and seat numbers must be positive.");
        }

        this.user = user;
        this.movie = movie;
        this.date = date;
        this.row = row;
        this.seat = seat;
    }

    // Identifying and linking
    @Override
    public String getUser() {return user;}     // who booked the reservation

    // Movie and scheduling
    @Override
    public String getMovie() {return movie;}

    @Override
    public LocalDateTime getDateTime() {return date;}

    // Seating info
    @Override
    public int getRow() {return row;}

    @Override
    public int getSeat() {return seat;}

    // Status and actions
    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void cancel() {
        active = false;
    }

    //Overrides .equals() to check if two people are trying to reserve the same time, seat, and movie
    @Override
    public boolean equals(Object o) {
        if (this == o) { //quick check for equality
            return true;
        } else if (o == null) {
            return false;
        }
        BasicReservation other = (BasicReservation) o; //casting object to use getters

        if (    movie.equals(other.getMovie()) &&
                date.equals(other.getDate()) &&
                row == other.getRow() &&
                seat == other.getSeat()) {
            return true;
        } else {
            return false;
        }
    }

    //Need to override hashcode if I override .equals() so that only equivalent objects have same hash
    @Override
    public int hashCode() {
        return Objects.hash(movie, date, row, seat);
    }
}
