package ReservationClasses;

public interface Reservation {

    String getUser(); // Attendee; who booked the reservation
    String getShowtime();
    boolean isActive();

    void cancel();

}
