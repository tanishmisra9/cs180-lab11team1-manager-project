package src;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface defining operations for the reservation database system.
 * Manages users, auditoriums, and reservations.
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Tanish Misra
 * @version Nov 19, 2025
 */
public interface ReservationDatabaseInterface {

    /**
     * Reserves a seat in an auditorium for a user.
     * @param user the user making the reservation
     * @param r the reservation details
     * @return true if successful, false otherwise
     */
    boolean reserve(BasicUser user, BasicReservation r);

    /**
     * Gets all reservations for a specific user.
     * @param user the user to query
     * @return list of reservations, or null if user not found
     */
    List<BasicReservation> getReservations(BasicUser user);

    /**
     * Adds an auditorium to the database.
     * @param a the auditorium to add
     */
    void addAuditorium(Auditorium a);

    /**
     * Gets all auditoriums in the database.
     * @return list of all auditoriums
     */
    List<Auditorium> getAuditoriums();

    /**
     * Adds a user to the database.
     * @param user the user to add
     */
    void addUser(BasicUser user);

    /**
     * Gets all users in the database.
     * @return list of all users
     */
    List<BasicUser> getUsers();

    /**
     * Finds a user by their username.
     * @param username the username to search for
     * @return the user if found, null otherwise
     */
    BasicUser getUserByUsername(String username);

    /**
     * Saves the database to persistent storage.
     */
    void saveData();

    /**
     * Edits the showing time of an auditorium.
     * @param oldTime the current showing time
     * @param newTime the new showing time
     * @return true if successful, false if auditorium not found
     */
    boolean editShowingTime(LocalDateTime oldTime, LocalDateTime newTime);

    /**
     * Deletes an auditorium from the database.
     * @param time the showing time of the auditorium to delete
     * @return true if successful, false if auditorium not found
     */
    boolean deleteAuditorium(LocalDateTime time);

    /**
     * Edits the movie name of an auditorium.
     * @param time the showing time of the auditorium
     * @param newName the new movie name
     * @return true if successful, false if auditorium not found
     */
    boolean editMovieName(LocalDateTime time, String newName);

    /**
     * Creates a new auditorium and adds it to the database.
     * @param rows number of rows in the auditorium
     * @param cols number of columns in the auditorium
     * @param price ticket price
     * @param movie movie name
     * @param time showing time
     * @return the created auditorium
     */
    Auditorium createAuditorium(int rows, int cols, double price, String movie, LocalDateTime time);

    /**
     * Populates the database with default auditoriums for the next 3 months.
     */
    void populateDefaults();
}