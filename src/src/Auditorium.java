package src;

import java.io.Serializable;
import java.time.*;
import java.util.Arrays;

/**
 * The Auditorium class of CS180 Team Project.
 * Auditoriums contain a jagged array of seats, where a seat is either "empty" or the name of the
 * user that fills said seat for the event day. double[][] seatPrices corresponds to each seat's price.
 * "The struggle towards the heights is enough to fill a man's heart" - albert camus
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Logan Dalton
 * @version Nov 19, 2025
 */
public class Auditorium implements AuditoriumInterface, Serializable {
    private static final long serialVersionUID = 1L;

    //----FIELDS----//
    private String[][] seats;
    private double[][] seatPrices;

    private String showingName = "";
    private LocalDateTime date = LocalDateTime.now();
    private LocalDateTime showingTime;


    //----CONSTRUCTORS----//

    public Auditorium(int[] rowLengths) {
        int row = rowLengths.length;
        seats = new String[row][];

        for (int i = 0; i < row; i++) {
            seats[i] = new String[rowLengths[i]];
        }
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seats[i][j] = "empty";
            }
        }

        seatPrices = new double[row][];
        for (int i = 0; i < row; i++) {
            seatPrices[i] = new double[rowLengths[i]];
        }

        for (int i = 0; i < seatPrices.length; i++) {
            for (int j = 0; j < seatPrices[i].length; j++) {
                seatPrices[i][j] = 0.00;
            }
        }

    }


    public Auditorium(String[][] seats) {
        this.seats = new String[seats.length][];

        for (int i = 0; i < seats.length; i++) {
            this.seats[i] = new String[seats[i].length];
            Arrays.fill(this.seats[i], "empty"); // Initialize the new row

        }

        seatPrices = new double[this.seats.length][];
        for (int i = 0; i < this.seats.length; i++) {
            seatPrices[i] = new double[seats[i].length];
        }

        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seatPrices[i][j] = 0.00;
            }
        }
    }

    public Auditorium(int rows, int cols) {
        seats = new String[rows][cols];
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seats[i][j] = "empty";
            }
        }
        seatPrices = new double[rows][cols];
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seatPrices[i][j] = 0.00;
            }
        }
    }

    public Auditorium(int[] rowLengths, double prices) {
        int row = rowLengths.length;
        seats = new String[row][];

        for (int i = 0; i < row; i++) {
            seats[i] = new String[rowLengths[i]];
        }
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seats[i][j] = "empty";
            }
        }

        seatPrices = new double[row][];
        for (int i = 0; i < row; i++) {
            seatPrices[i] = new double[rowLengths[i]];
        }

        for (int i = 0; i < seatPrices.length; i++) {
            for (int j = 0; j < seatPrices[i].length; j++) {
                seatPrices[i][j] = prices;
            }
        }

    }

    public Auditorium(String[][] seats, double prices) {
        this.seats = new String[seats.length][];
        this.seatPrices = new double[seats.length][];


        for (int i = 0; i < seats.length; i++) {

            this.seats[i] = new String[seats[i].length];
            Arrays.fill(this.seats[i], "empty");

            this.seatPrices[i] = new double[seats[i].length];
            Arrays.fill(this.seatPrices[i], prices);
        }
    }

    public Auditorium(int rows, int cols, double prices) {
        seats = new String[rows][cols];
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seats[i][j] = "empty";
            }
        }
        seatPrices = new double[rows][cols];
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seatPrices[i][j] = prices;
            }
        }
    }

    public Auditorium(int rows, int cols, double prices, LocalDateTime date) {
        this(rows, cols, prices);
        this.date = date;
    }

    public Auditorium(int rows, int cols, double price, String movie, LocalDateTime showingTime) {
        this(rows, cols, price);
        this.showingName = movie;
        this.showingTime = showingTime;
    }

    public Auditorium(int[] rowLengths, double price, String movie, LocalDateTime showingTime) {
        this(rowLengths, price);  // existing jagged constructor
        this.showingName = movie;
        this.showingTime = showingTime;
    }


    //----METHODS---//

    //--Getters/Setters--//

    @Override
    public String getShowingName() {
        return showingName;
    }

    @Override
    public void setShowingName(String newName) {
        showingName = newName;
    }


    // Movie methods are a quick fix to resolve issues with other classes using getMovie instead of getShowing
    public String getMovie() {
        return showingName;
    }

    public void setMovie(String newName) {
        showingName = newName;
    }

    public LocalDateTime getMovieDate() {
        return showingTime;
    }



    @Override
    public String[][] getSeats() {
        String[][] copy = new String[this.seats.length][];
        for (int i = 0; i < this.seats.length; i++) {
            copy[i] = Arrays.copyOf(this.seats[i], this.seats[i].length);
        }
        return copy;
    }

    @Override
    public double[][] getSeatPrices() {
        double[][] copy = new double[this.seatPrices.length][];
        for (int i = 0; i < this.seatPrices.length; i++) {
            copy[i] = Arrays.copyOf(this.seatPrices[i], this.seatPrices[i].length);
        }
        return copy;
    }

    public double getSeatPrice(int row, int col) {
        return seatPrices[row][col];
    }

    @Override
    public int getRowNumber() {
        return seats.length;
    }

    @Override
    public int getColumnNumber() {
        if (seats == null || seats.length == 0) {
            return 0;
        }

        int maxCols = 0;

        for (String[] row : seats) {
            // 3. Check for null rows in the jagged array
            if (row != null) {
                if (row.length > maxCols) {
                    maxCols = row.length;
                }
            }
        }

        return maxCols;
    }

    public LocalDateTime getShowingTime() {
        return showingTime;
    }

    public LocalDateTime getShowingDate() {
        return showingTime;
    }

    @Override
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public void setSeats(String[][] newSeats) {

        this.seats = new String[newSeats.length][];
        this.seatPrices = new double[newSeats.length][];


        for (int i = 0; i < newSeats.length; i++) {

            this.seats[i] = Arrays.copyOf(newSeats[i], newSeats[i].length);
            this.seatPrices[i] = new double[newSeats[i].length];
            Arrays.fill(this.seatPrices[i], 0.0); // Initialize to default

        }
    }


    @Override
    public void setSeatPrices(double[][] newSeatPrices) {

        this.seats = new String[newSeatPrices.length][];
        this.seatPrices = new double[newSeatPrices.length][];


        for (int i = 0; i < newSeatPrices.length; i++) {

            this.seatPrices[i] = Arrays.copyOf(newSeatPrices[i], newSeatPrices[i].length);

            this.seats[i] = new String[newSeatPrices[i].length];
            Arrays.fill(this.seats[i], "empty");

        }
    }

    //--Note that this will set SeatPrices to zero.
    @Override
    public void setSeats(int row, int col) {
        seats = new String[row][col];
        seatPrices = new double[row][col];

        for (int i = 0; i < row; i++) {
            Arrays.fill(seats[i], "empty");
            Arrays.fill(seatPrices[i], 0.0);
        }
    }


    @Override
    public void setShowingDate(LocalDateTime showingTime) {
        this.showingTime = showingTime;
    }

    @Override
    public void setShowingDate(int year, int month, int day, int hour, int minute) {
        showingTime = LocalDateTime.of(year, month, day, hour, minute);
    }

    public void setShowingTime(LocalDateTime showingTime) {
        this.showingTime = showingTime;
    }

    public void setShowingTime(int year, int month, int day, int hour, int minute) {
        showingTime = LocalDateTime.of(year, month, day, hour, minute);
    }




    //--Updating the current time--//

    @Override
    public void updateTime(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public void updateTime() {
        this.date = LocalDateTime.now();
    }

    @Override
    public void updateTime(int year, int month, int day, int hour, int minute) {
        date = LocalDateTime.of(year, month, day, hour, minute);
    }

    //--Updating the showing time--//

    @Override
    public void updateShowingTime(LocalDateTime newDate) {
        if (newDate.isAfter(date)) {
            showingTime = newDate;
        } else {
            System.out.println("updateShowingTime error: invalid date:" +
                    "Date is before current time or exactly current time.");
        }

    }

    @Override
    public void updateShowingTime() {
        LocalDateTime newDate = LocalDateTime.now();
        if (newDate.isAfter(date)) { // <--- ADDED: Check
            showingTime = newDate;
        } else {
            System.out.println("updateShowingTime error: invalid date:" +
                    "Current time is not after the set date.");
        }
    }

    @Override
    public void updateShowingTime(int year, int month, int day, int hour, int minute) {
        LocalDateTime newDate = LocalDateTime.of(year, month, day, hour, minute);
        if (newDate.isAfter(date)) {
            showingTime = newDate;
        } else {
            System.out.println("updateShowingTime error: invalid date:" +
                    "Date is before current time or exactly current time.");
        }

    }


    //--Sets the price of a particular seat to the inputted price--//
    @Override
    public void setPrice(int row, int col, double price) {
        try {
            if (price >= 0) {
                seatPrices[row][col] = price;
            } else {
                System.out.println("setPrice error: Price cannot be negative.");
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("setPrice: Warning: row/col " + row + "," + col + " is out of bounds.");
        }

    }


    //--Sets all seat prices to the inputted price--//
    //-Iterates over each row and uses Arrays.fill() on double[] array-//
    @Override
    public void setAllPrices(double price) {
        if (price >= 0) {
            for (double[] seatPrice : seatPrices) {
                if (seatPrice != null) {
                    Arrays.fill(seatPrice, price);
                }
            }
        } else {
            System.out.println("setAllPrices error: price cannot be negative.");
        }

    }


    //--Increases all prices by the inputted price--//
    @Override
    public void increaseAllPrices(double price) {
        for (int i = 0; i < seatPrices.length; i++) {
            for (int j = 0; j < seatPrices[i].length; j++) {
                if ((seatPrices[i][j] + price) >= 0) {
                    seatPrices[i][j] += price;
                } else {
                    System.out.println("increaseAllPrices error: new price cannot be negative.");
                }
            }
        }
    }


    //--Multiplies prices by the inputted amount--//
    @Override
    public void multiplyAllPrices(double price) {
        for (int i = 0; i < seatPrices.length; i++) {
            for (int j = 0; j < seatPrices[i].length; j++) {
                if ((seatPrices[i][j] * price) >= 0) {
                    seatPrices[i][j] *= price;
                } else {
                    System.out.println("multiplyAllPrices error: new price cannot be negative.");
                }

            }
        }
    }

    //--Sets the prices of an entire row to the inputted price--//
    @Override
    public void setRowPrices(int row, double price) {
        try {
            if (price >= 0) {
                Arrays.fill(seatPrices[row], price);
            } else {
                System.out.println("setRowPrices error: price cannot be negative.");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("setRowPrices: Warning: row " +
                    row + " is out of bounds.");
        }
    }

    //--sets the prices of an entire column to the inputted price--//
    @Override
    public void setColPrices(int col, double price) {
        for (int i = 0; i < seatPrices.length; i++) {
            try {
                if (price >= 0) {
                    seatPrices[i][col] = price;
                } else {
                    System.out.println("setColPrices error: price cannot be negative.");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Warning: column " + col +
                        " is out of bounds for column " + i + ".");
            }
        }
    }

    //--Increases the prices of a row by the price--//
    @Override
    public void increaseRowPrices(int row, double price) {
        try {
            for (int i = 0; i < seatPrices[row].length; i++) {
                if ((seatPrices[row][i] + price) >= 0) {
                    seatPrices[row][i] += price;
                } else {
                    System.out.println("increaseRowPrices error: new price cannot be negative.");
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Warning: row " + row + " is out of bounds.");
        }
    }


    //--Multiplies the prices of a row by the given price--//
    @Override
    public void multiplyRowPrices(int row, double price) {
        try {
            for (int i = 0; i < seatPrices[row].length; i++) {
                if ((seatPrices[row][i] * price) >= 0) {
                    seatPrices[row][i] *= price;
                } else {
                    System.out.println("multiplyRowPrices error: new price cannot be negative.");
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Warning: row " + row + " is out of bounds.");
        }
    }


    //--Increases the prices of an entire column--//
    @Override
    public void increaseColPrices(int col, double price) {
        for (int i = 0; i < seatPrices.length; i++) {
            try {
                if ((seatPrices[i][col] + price) >= 0) {
                    seatPrices[i][col] += price;
                } else {
                    System.out.println("increaseColPrices error: new price cannot be negative for row " + i);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Warning: column " + col
                        + " is out of bounds for row " + i + ".");
            }
        }
    }

    //--Multiplies the prices of a col by the given price--//
    @Override
    public void multiplyColPrices(int col, double price) {
        for (int i = 0; i < seatPrices.length; i++) {
            try {
                if ((seatPrices[i][col] * price) >= 0) {
                    seatPrices[i][col] *= price;
                } else {
                    System.out.println("multiplyColPrices error: new price cannot be negative for row " + i);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Warning: column " + col
                        + " is out of bounds for row " + i + ".");
            }
        }
    }

    //--Sets a seat to a user's name--//
    @Override
    public boolean setReservation(String user, int row, int col) {
        try {

            if (seats[row][col].equals("empty")) {
                seats[row][col] = user;
                return true;
            } else {
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("setReservation: Warning: row " + row
                    + " column " + col +
                    " is out of bounds.");
        }
        return false;
    }

    //--Returns true if a seat is available--//
    @Override
    public boolean checkSeat(int row, int col) {
        try {
            return seats[row][col].equals("empty");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("checkSeat: Warning: row " + row
                    + " column " + col +
                    " is out of bounds.");
            return false;
        }
    }


    //--Returns true if a seat is valid--//
    @Override
    public boolean isValidSeat(int row, int col) {
        if (row < 0 || row >= seats.length) return false;
        if (seats[row] == null || col < 0 || col >= seats[row].length) return false;

        return true;
    }

} //end class