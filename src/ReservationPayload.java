package src;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReservationPayload implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String movie;
    private final String showtime;
    private final int startRow;
    private final int startSeat;
    private final int numPeople;
    private final double reservationFee;
    private final LocalDateTime reservationDate;

    public ReservationPayload(String movie, String showtime, int startRow, int startSeat,
                              int numPeople, double reservationFee,  LocalDateTime reservationDate) {
        this.movie = movie;
        this.showtime = showtime;
        this.startRow = startRow;
        this.startSeat = startSeat;
        this.numPeople = numPeople;
        this.reservationFee = reservationFee;
        this.reservationDate = reservationDate;
    }

    public String getMovie() { return movie; }
    public String getShowtime() { return showtime; }
    public int getStartRow() { return startRow; }
    public int getStartSeat() { return startSeat; }
    public int getNumPeople() { return numPeople; }
    public double getReservationFee() { return reservationFee; }
    public LocalDateTime getReservationDate() { return reservationDate; }
}
