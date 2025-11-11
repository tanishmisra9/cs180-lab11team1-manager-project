package src;

import java.time.LocalDate;

public interface Reservation {
    
    // Identifying and linking
    String getUser();       // who booked the reservation
    String getShowtime();   // showtime ID or descriptor

    // Movie and scheduling
    String getMovie();      
    LocalDate getDate();

    // Seating info
    int getRow();
    int getSeat();

    // Status and actions
    boolean isActive();
    void cancel();
}