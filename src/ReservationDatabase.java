package src;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReservationDatabase implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<User, List<Reservation>> reservationMap = new HashMap<>();
    private List<Auditorium> auditoriums = new ArrayList<>();

    public static ReservationDatabase loadDatabase() { // call on server startup 
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("db.dat"))) {
            return (ReservationDatabase) in.readObject();
        } catch (FileNotFoundException e) {
            return new ReservationDatabase();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveData() { // call on server sleep
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("db.dat"))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Reservation> getReservations(User u) {
        return reservationMap.getOrDefault(u, new ArrayList<>());
    }

    public void addAuditorium(Auditorium auditorum) {
	auditoriums.add(auditorum);
    }

    public synchronized boolean reserve(User user, Reservation r) {
	Auditorium target = null;
	for(Auditorium a: auditoriums) {
		if (a.getShowingName().equals(r.getMovie()) && a.getShowingDate().equals(r.getDateTime())) {
			target = a;
			break;
		}
	}
	
	if (target == null) {
		return false;
	}

	int row = r.getRow() - 1;
        int col = r.getSeat() - 1;

        if(!target.isValidSeat(row, col) || !target.checkSeat(row, col)) return false;

        target.setReservation(user.getUsername(), row, col);
        reservationMap.computeIfAbsent(user, k -> new ArrayList<>()).add(r);

        return true;
    }

}

