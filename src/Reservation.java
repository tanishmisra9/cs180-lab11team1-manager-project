package src;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Reservation interface defining core reservation operations.
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Tanish Misra
 * @version Nov 19, 2025
 */
public interface Reservation {

    // Identifying and linking
    String getUser(); // who booked the reservation
    String getShowtime(); // showtime ID or descriptor

    // Movie and scheduling
    String getMovie();
    LocalDate getDate();
    LocalDateTime getDateTime();

    // Seating info
    int getRow();
    int getSeat();

    // Status and actions
    boolean isActive();
    void cancel();
}