import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ReservationDatabase implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<User, List<Reservation>> reservationMap = new HashMap<>();
    private Map<String, Map<LocalDate, Set<String>>> seatMap = new HashMap<>();
    private Map<String, Map<LocalDate, Set<String>>> validSeats = new HashMap<>();

    public static ReservationDatabase loadDatabase() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("db.dat"))) {
            return (ReservationDatabase) in.readObject();
        } catch (FileNotFoundException e) {
            return new ReservationDatabase();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("db.dat"))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Reservation> getReservations(User u) {
        return reservationMap.getOrDefault(u, new ArrayList<>());
    }

    public synchronized boolean reserve(User user, Reservation r) {
        String seatKey = r.getRow() + r.getSeat();

        if (!isValidSeat(r.getMovie(), r.getDate(), r.getRow(), r.getSeat())) {
            return false;
        }

        if (isSeatTaken(r.getMovie(), r.getDate(), r.getRow(), r.getSeat())) {
            return false;
        }

        seatMap.computeIfAbsent(r.getMovie(), m -> new HashMap<>())
            .computeIfAbsent(r.getDate(), d -> new HashSet<>())
            .add(seatKey);

        reservationMap.computeIfAbsent(user, k -> new ArrayList<>()).add(r);

        return true;
    }

    public synchronized boolean isSeatTaken(String movie, LocalDate date, int row, int seat) {
        String seatKey = row + "" + seat;
        return seatMap.getOrDefault(movie, Map.of()).getOrDefault(date, Set.of()).contains(seatKey);
    }

    public synchronized boolean isValidSeat(String movie, LocalDate date, int row, int seat) {
        String seatKey = row + "" + seat;
        return validSeats.getOrDefault(movie, Map.of()).getOrDefault(date, Set.of()).contains(seatKey);
    }
}

