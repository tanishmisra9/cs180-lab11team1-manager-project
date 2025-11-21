package src;

import java.io.*;
import java.time.LocalDateTime;
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
    public void addAuditorium(Auditorium auditorium) {
        auditoriums.add(auditorium);
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
}