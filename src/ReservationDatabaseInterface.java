package src;

import java.util.List;

public interface ReservationDatabaseInterface {

    boolean reserve(Auditorium a, Reservation r);

    boolean cancelReservation(Reservation r);

    List<Reservation> getReservations();

    boolean saveDatabase();

    boolean loadDatabase();

    void addAuditorium(Auditorium a);

    List<Auditorium> getAuditoriums();
}
