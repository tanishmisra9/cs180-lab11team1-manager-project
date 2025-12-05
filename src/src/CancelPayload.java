package src;

import java.io.Serializable;

public class CancelPayload implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String movie;
    private final String showtime;
    private final int numPeople;
    private final double reservationFee;

    public CancelPayload(String movie, String showtime, int numPeople, double reservationFee) {
        this.movie = movie;
        this.showtime = showtime;
        this.numPeople = numPeople;
        this.reservationFee = reservationFee;
    }

    public String getMovie() { return movie; }
    public String getShowtime() { return showtime; }
    public int getNumPeople() { return numPeople; }
    public double getReservationFee() { return reservationFee; }
}
