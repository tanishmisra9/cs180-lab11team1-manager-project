package src;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Central database for managing users, reservations, and auditoriums.
 * Uses serialization to persist data to db.dat file.
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Sebastian Ting
 * @version Nov 19, 2025
 */
public class ReservationDatabase implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<BasicUser> users = new ArrayList<>();
    private List<Auditorium> auditoriums = new ArrayList<>();

    /**
     * Loads the database from db.dat file.
     * If file doesn't exist, returns a new empty database.
     *
     * @return the loaded database or a new one if file not found
     */
    public static ReservationDatabase loadDatabase() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("db.dat"))) {
            return (ReservationDatabase) in.readObject();
        } catch (FileNotFoundException e) {
            return new ReservationDatabase();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the database to db.dat file.
     */
    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("db.dat"))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all reservations for a specific user.
     *
     * @param u the user to get reservations for
     * @return list of reservations for the user, or null if user not found
     */
    public List<BasicReservation> getReservations(BasicUser u) {
        for (BasicUser it : users) {
            if (u.equals(it)) return it.getReservations();
        }
        return null;
    }

    /**
     * Adds an auditorium to the database.
     *
     * @param auditorium the auditorium to add
     */
    public void addAuditorium(Auditorium a) {
        int idx = findInsertIndex(a.getShowingTime());
        auditoriums.add(idx, a);
    }


    /**
     * Adds a user to the database.
     *
     * @param user the user to add
     */
    public void addUser(BasicUser user) {
        users.add(user);
    }

    /**
     * Attempts to reserve a seat for a user.
     * Finds matching auditorium by movie name and showtime, validates seat,
     * and creates reservation if successful.
     *
     * @param user the user making the reservation
     * @param r the reservation to make
     * @return true if reservation successful, false otherwise
     */
    public synchronized boolean reserve(BasicUser user, BasicReservation r) {
        Auditorium target = null;
        for (Auditorium a : auditoriums) {
            if (a.getShowingName().equals(r.getMovie()) && a.getShowingDate().isEqual(r.getDateTime())) {
                target = a;
                break;
            }
        }

        if (target == null) {
            return false;
        }

        int row = r.getRow() - 1;
        int col = r.getSeat() - 1;

        if (!target.isValidSeat(row, col) || !target.checkSeat(row, col)) {
            return false;
        }

        target.setReservation(user.getUsername(), row, col);
        user.addReservation(r.getMovie(), r.getDateTime(), r.getRow(), r.getSeat(), r.getPeople(), target.getSeatPrice(row, col));
        return true;
    }

    /**
     * Gets all users in the database.
     *
     * @return a copy of the users list
     */
    public List<BasicUser> getUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return the user if found, null otherwise
     */
    public BasicUser getUserByUsername(String username) {
        for (BasicUser user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Gets all auditoriums in the database.
     *
     * @return a copy of the auditoriums list
     */
    public List<Auditorium> getAuditoriums() {
        return new ArrayList<>(auditoriums);
    }

    private Auditorium findByTime(LocalDateTime time) {
        for (Auditorium a : auditoriums) {
            if (a.getShowingTime().equals(time)) return a;
        }
        return null;
    }

    public boolean editShowingTime(LocalDateTime oldTime, LocalDateTime newTime) {
        Auditorium a = findByTime(oldTime);
        if (a == null) return false;

        auditoriums.remove(a);
        a.setShowingTime(newTime);

        addAuditorium(a);

        return true;
    }

    public boolean deleteAuditorium(LocalDateTime time) {
        Auditorium a = findByTime(time);
        if (a == null) return false;

        auditoriums.remove(a);
        return true;
    }


    public boolean editMovieName(LocalDateTime time, String newName) {
        Auditorium a = findByTime(time);
        if (a == null) return false;

        a.setShowingName(newName);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a");
        String pretty = a.getShowingTime().format(fmt);
        System.out.println(pretty);
        return true;
    }


    private int findInsertIndex(LocalDateTime time) {
        for (int i = 0; i < auditoriums.size(); i++) {
            if (time.isBefore(auditoriums.get(i).getShowingTime())) {
                return i;
            }
        }
        return auditoriums.size(); // append at end
    }

    public Auditorium createAuditorium(int rows, int cols, double price,
                                       String movie, LocalDateTime time) {

        Auditorium a = new Auditorium(rows, cols, price, movie, time);
        addAuditorium(a);
        return a;
    }


    public void populateDefaults() {
        auditoriums.clear();

        LocalDate today = LocalDate.now();
        LocalDate end = today.plusMonths(3);

        String[] movies = {
                "Dune 2",
                "Interstellar",
                "Oppenheimer",
                "Spider-Man: Across the Spider-Verse",
                "Parasite",
                "Inception"
        };

        Random rand = new Random();

        // Standard showtimes
        LocalTime[] times = {
                LocalTime.of(13, 0), // 1pm
                LocalTime.of(16, 0), // 4pm
                LocalTime.of(19, 0)  // 7pm
        };

        for (LocalDate date = today; !date.isAfter(end); date = date.plusDays(1)) {

            // Random movie of the day
            String movieOfDay = movies[rand.nextInt(movies.length)];

            // Number of auditoriums that day (2–3)
            int count = rand.nextInt(2) + 2;

            for (int i = 0; i < count; i++) {
                LocalTime time = times[i % times.length];  // just cycle through times
                LocalDateTime showDateTime = LocalDateTime.of(date, time);

                Auditorium a = new Auditorium(
                        10,     // rows
                        20,     // cols
                        4.0,    // ticket price
                        movieOfDay,
                        showDateTime
                );

                auditoriums.add(a);
            }
        }

        this.saveData();
    }



}