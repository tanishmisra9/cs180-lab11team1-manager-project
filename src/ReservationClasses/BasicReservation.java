import java.time.LocalDate;
import java.io.Serializable;
import java.util.Objects;

public class BasicReservation implements Reservation, Serializable {
    //fields
    private String user;
    private String showTime;
    private String movie;
    private LocalDate date;
    private int row;
    private int seat;
    private boolean active = true;

    //constructor
    public BasicReservation(String user, String showTime, String movie, LocalDate date, int row, int seat) {
        this.user = user;
        this.showTime = showTime;
        this.movie = movie;
        this.date = date;
        this.row = row;
        this.seat = seat;
    }

    // Identifying and linking
    @Override
    public String getUser() {return user;}       // who booked the reservation

    @Override
    public String getShowtime() {return showTime;}   // showtime ID or descriptor

    // Movie and scheduling
    @Override
    public String getMovie() {return movie;}

    @Override
    public LocalDate getDate() {return date;}

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
        } else {
            BasicReservation other = (BasicReservation) o; //casting object to use getters
        }

        if (showTime.equals(other.getShowtime()) &&
                movie.equals(other.getMovie()) &&
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
        return Objects.hash(showTime, movie, date, row, seat);
    }
}