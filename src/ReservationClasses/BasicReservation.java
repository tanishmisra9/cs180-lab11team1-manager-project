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
