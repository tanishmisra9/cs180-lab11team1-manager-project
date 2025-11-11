package src;

import java.time.LocalDateTime;

/**
 * An interface defining the public contract for an Auditorium of CS180 Team Project 01.
 * This includes methods for managing seats, prices, and showing times.
 *
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 * Team Project 01
 *
 * @author Logan Dalton
 * @version Nov 10, 2025
 */
/**

 */

public interface AuditoriumInterface {

    //----Getters/Setters----//

    /**
     * Gets the String representing the showing name.
     * @return the name of the showing.
     */
    String getShowingName();


    /**
     * Sets the String showingName to the newName.
     * This changes the name of the showing.
     * @param newName the new name of the showing.
     */
    void setShowingName (String newName);

    /**
     //Gets the 2D array representing the seats and their reservation status.
     //A seat is "empty" if empty.
     //A seat is the User's name if filled.
     //@return A 2D String array of seats.
     */
    String[][] getSeats();

    /**
     * Gets the 2D array representing the prices for each seat.
     * @return A 2D double array of seat prices.
     */
    double[][] getSeatPrices();

    /**
     * Gets the amount of rows of seats.
     * @return an int of the amount of rows.
     */
    int getRowNumber();

    /**
     * Gets the amount of columns of seats.
     * Gets the largest column of seats if array is jagged.
     * @return the largest column of seats.
     */
    int getColumnNumber();

    /**
     * Gets the scheduled date and time of the showing.
     * @return The showing's LocalDateTime.
     */
    LocalDateTime getShowingDate();

    /**
     * Gets the current date and time (using LocalDateTime).
     * @return The current LocalDateTime.
     */
    LocalDateTime getDate();

    /**
     * Sets the current date and time (updates it).
     * @param date The LocalDateTime to set as current.
     */
    void setDate(LocalDateTime date);

    /**
     * Replaces the existing seat layout with a new one.
     * @param seats The new 2D String array of seats.
     */
    void setSeats(String[][] seats);

    /**
     * Replaces the existing seat price layout with a new one.
     * @param seatPrices The new 2D double array of seat prices.
     */
    void setSeatPrices(double[][] seatPrices);

    /**
     * Re-initializes the seats array with new dimensions.
     * Useful when not using a jagged array.
     * @param row The number of rows.
     * @param col The number of columns.
     */
    void setSeats(int row, int col);

    /**
     * Sets the showing date and time using LocalDateTime.
     * @param showingDate The date and time of the showing.
     */
    void setShowingDate(LocalDateTime showingDate);

    /**
     * Sets the showing date and time using individual components.
     * Useful if showing date is stored as ints.
     * @param year The year of the showing.
     * @param month The month of the showing.
     * @param day The day of the showing.
     * @param hour The hour of the showing.
     * @param minute The minute of the showing.
     */
    void setShowingDate(int year, int month, int day, int hour, int minute);


    //----Updating the current time----//

    /**
     * Updates the current time with a LocalDateTime.
     * @param date The new date and time.
     */
    void updateTime(LocalDateTime date);

    /**
     * Updates the current time to the system's current time (now).
     */
    void updateTime();

    /**
     * Updates the current time using individual components.
     * @param year The year.
     * @param month The month.
     * @param day The day.
     * @param hour The hour.
     * @param minute The minute.
     */
    void updateTime(int year, int month, int day, int hour, int minute);


    //---- Updating the showing time ----//

    /**
     * Updates the showing time with a specific LocalDateTime.
     * @param date The new showing date and time.
     */
    void updateShowingTime(LocalDateTime date);

    /**
     * Updates the showing time to the system's current time (now).
     */
    void updateShowingTime();

    /**
     * Updates the showing time using individual components.
     * * Useful if showing date is stored as ints.
     * @param year The year.
     * @param month The month.
     * @param day The day.
     * @param hour The hour.
     * @param minute The minute.
     */
    void updateShowingTime(int year, int month, int day, int hour, int minute);


    //----Price Management----//

    /**
     * Sets the price of a specific seat.
     * @param row The row of the seat.
     * @param col The column of the seat.
     * @param price The price to set for the seat.
     */
    void setPrice(int row, int col, double price);

    /**
     * Sets the price for all seats in the auditorium.
     * @param price The price to set for all seats.
     */
    void setAllPrices(double price);

    /**
     * Increases the price of all seats by a fixed amount.
     * Use a negative number to decrease.
     * @param price The amount to add to each seat's price.
     */
    void increaseAllPrices(double price);

    /**
     * Multiplies the price of all seats by a given factor.
     * @param price The factor to multiply each seat's price by.
     */
    void multiplyAllPrices(double price);

    /**
     * Sets the price for all seats in a specific row.
     * @param row The row index.
     * @param price The price to set for the entire row.
     */
    void setRowPrices(int row, double price);

    /**
     * Sets the price for all seats in a specific column.
     * @param col The column index.
     * @param price The price to set for the entire column.
     */
    void setColPrices(int col, double price);

    /**
     * Increases the price of all seats in a specific row by a fixed amount.
     * Use a negative number to decrease.
     * @param row The row index.
     * @param price The amount to add to each seat's price in that row.
     */
    void increaseRowPrices(int row, double price);

    /**
     * Multiplies the price of all seats in a specific row by a given factor.
     * @param row The row index.
     * @param price The factor to multiply each seat's price in that row by.
     */
    void multiplyRowPrices(int row, double price);

    /**
     * Increases the price of all seats in a specific column by a fixed amount.
     * Use a negative number to decrease.
     * @param col The column index.
     * @param price The amount to add to each seat's price in that column.
     */
    void increaseColPrices(int col, double price);

    /**
     * Multiplies the price of all seats in a specific column by a given factor.
     * @param row The row index.
     * @param price The factor to multiply each seat's price in that row by.
     */
    void multiplyColPrices(int row, double price);


    //----Reservation Management----//

    /**
     * Attempts to reserve a seat for a user.
     * @param user The name or ID of the user reserving the seat.
     * @param row The row of the seat.
     * @param col The column of the seat.
     * @return true if the seat was successfully reserved ("empty"), false otherwise.
     */
    boolean setReservation(String user, int row, int col);

    /**
     * Checks if a specific seat is available ("empty").
     * @param row The row of the seat.
     * @param col The column of the seat.
     * @return true if the seat is "empty", false otherwise.
     */
    boolean checkSeat(int row, int col);

    /**
     * Checks if a specific seat is a valid seat.
     * @param row The row of the seat.
     * @param col The column of the seat.
     * @return true if seat is valid, false otherwise.
     */
    boolean isValidSeat(int row, int col);
}
