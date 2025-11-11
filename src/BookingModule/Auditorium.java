import java.time.*;
import java.util.Arrays;

/**
 * The Auditorium class of CS180 Team Project.
 * Auditoriums contain a jagged array of seats, where a seat is either "empty" or the name of the
 * user that fills said seat for the event day. double[][] seatPrices corresponds to each seat's price.
 *
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Logan Dalton
 * @version Nov 8, 2025
 */

public class Auditorium {

    //----FIELDS----//
    private String[][] seats;
    private double[][] seatPrices;

    private LocalDateTime date = LocalDateTime.now();
    private LocalDateTime showingDate;
    private String movie; 

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
        
	movie = "";
    }

    public Auditorium(String[][] seats) {
        this.seats = seats;

        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seats[i][j] = "empty";
            }
        }

        seatPrices = new double[this.seats.length][];

        for (int i = 0; i < this.seats.length; i++) {

            if (this.seats[i] != null) {
                seatPrices[i] = new double[seats[i].length];

            } else {
                seatPrices[i] = null;
            }

        }

	movie = "";
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

	movie = "";
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

	movie = "";

    }

    public Auditorium(String[][] seats, double prices) {
        this.seats = seats;

        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seats[i][j] = "empty";
            }
        }

        seatPrices = new double[this.seats.length][];

        for (int i = 0; i < this.seats.length; i++) {

            if (this.seats[i] != null) {
                seatPrices[i] = new double[seats[i].length];
                Arrays.fill(seatPrices[i], prices);

            } else {
                seatPrices[i] = null;
            }
        }

	movie = "";
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

	movie = "";
    }

    public Auditorium(int rows, int cols, double price, String movie, LocalDateTime showingTime) {
        this(rows, cols, price);
	this.movie = movie;
        this.showingTime = showingTime;
    }

    public Auditorium(int[] rowLengths, double price, String movie, LocalDateTime showingTime) {
        this(rowLengths, price);  // existing jagged constructor
        this.movie = movie;
        this.showingTime = showingTime;
    }


    //----METHODS---//

    //--Getters/Setters--//

    public String[][] getSeats() {
        return seats;
    }

    public double[][] getSeatPrices() {
        return seatPrices;
    }

    public LocalDateTime getShowingDate() {
        return showingDate;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getRowNumber() {
        return seats.length;
    }

    public int getColumnNumber() {
        return seats[0].length;
    } 

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setSeats(String[][] seats) {
        this.seats = seats;
    }

    public void setSeatPrices(double[][] seatPrices) {
        this.seatPrices = seatPrices;
    }

    public void setSeats(int row, int col) {
        seats = new String[row][col];
    }

    public void setPrices(int row, int col) {
        seatPrices = new double[row][col];
    }

    public void setShowingDate(LocalDateTime showingDate) {
        this.showingDate = showingDate;
    }

    public void setShowingDate(int year, int month, int day, int hour, int minute) {
        showingDate = LocalDateTime.of(year, month, day, hour, minute);
    }


    //--Updating the current time--//

    public void updateTime(LocalDateTime date) {
        this.date = date;
    }

    public void updateTime() {
        this.date = LocalDateTime.now();
    }

    public void updateTime(int year, int month, int day, int hour, int minute) {
        date = LocalDateTime.of(year, month, day, hour, minute);
    }

    //--Updating the showing time--//
    public void updateShowingTime(LocalDateTime date) {
        showingDate = date;
    }

    public void updateShowingTime() {
        showingDate = LocalDateTime.now();
    }

    public void updateShowingTime(int year, int month, int day, int hour, int minute) {
        showingDate = LocalDateTime.of(year, month, day, hour, minute);
    }


    // sets the price of a particular seat to the inputted price
    public void setPrice(int row, int col, double price) {
        try {
            seatPrices[row][col] = price;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Warning: row/col " + row + "," + col + " is out of bounds.");
        }

    }


    // Sets all seat prices to the inputted price.
    // Iterates over each row and uses Arrays.fill() on double[] array.
    public void setAllPrices(double price) {
        for (int i = 0; i < seatPrices.length; i++) {
            if (seatPrices[i] != null) {
                Arrays.fill(seatPrices[i], price);
            }
        }
    }


    // increases all prices by the inputted price
    public void increaseAllPrices(double price) {
        for (int i = 0; i < seatPrices.length; i++) {
            for (int j = 0; j < seatPrices[i].length; j++) {
                seatPrices[i][j] += price;
            }
        }
    }


    // multiplies prices by the inputted amount
    public void multiplyAllPrices(double price) {
        for (int i = 0; i < seatPrices.length; i++) {
            for (int j = 0; j < seatPrices[i].length; j++) {
                seatPrices[i][j] *= price;
            }
        }
    }

    // sets the prices of an entire row to the inputted price
    public void setRowPrices(int row, double price) {
        try {
            Arrays.fill(seatPrices[row], price);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Warning: row " + row + " is out of bounds.");
        }
    }

    // sets the prices of an entire column to the inputted price
    public void setColPrices(int col, double price) {
        for (int i = 0; i < seatPrices.length; i++) {
            try {
                seatPrices[i][col] = price;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Warning: column " + col + " is out of bounds for row " + i + ".");
            }
        }
    }

    // increases the prices of a row by the price
    public void increaseRowPrices(int row, double price) {
        for (int i = 0; i < seatPrices[row].length; i++) {
            try {
                seatPrices[row][i] += price;
            } catch (ArrayIndexOutOfBoundsException e) {
            }

        }
    }

    // multiplies the prices of a row by the given price
    public void multiplyRowPrices(int row, double price) {
        for (int i = 0; i < seatPrices[row].length; i++) {
            try {
                seatPrices[row][i] *= price;
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
    }

    // increases the prices of a col
    public void increaseColPrices(int col, double price) {
        for (int i = 0; i < seatPrices.length; i++) {
            try {
                seatPrices[i][col] += price;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Warning: column " + col + " is out of bounds for row " + i + ".");
            }
        }
    }

    //sets a seat to a user's name.
    public boolean setReservation(String user, int row, int col) {
        if (seats[row][col].equals("empty")) {

            seats[row][col] = user;
            return true;

        } else {
            return false;
        }
    }

    //returns true if a seat is available
    public boolean checkSeat(int row, int col) {
        return seats[row][col].equals("empty");
    }

    public boolean isValidSeat(int row, int col) {
        if(row < 0 || row >= seats.length) return false;
	if(seats[row] == null || col < 0 || col >= seats[row].length) return false;

        return true;
    }

}
