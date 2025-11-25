package src;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;



// ---------------------------
// Auditorium (seating) payload
// ---------------------------
class AuditoriumPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String[][] seats;

    public AuditoriumPayload(String[][] seats) {
        this.seats = seats;
    }

    public String[][] getSeats() {
        return seats;
    }
}

// ---------------------------
// Reserve a seat
// ---------------------------
class ReserveSeatPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String movieName;
    private int row;
    private int col;
    private String username;

    public ReserveSeatPayload(String movieName, int row, int col, String username) {
        this.movieName = movieName;
        this.row = row;
        this.col = col;
        this.username = username;
    }

    public String getMovieName() { return movieName; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public String getUsername() { return username; }
}

// ---------------------------
// Edit showing name
// ---------------------------
class EditShowingNamePayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String oldMovieName;
    private String newMovieName;

    public EditShowingNamePayload(String oldMovieName, String newMovieName) {
        this.oldMovieName = oldMovieName;
        this.newMovieName = newMovieName;
    }

    public String getOldMovieName() { return oldMovieName; }
    public String getNewMovieName() { return newMovieName; }
}

// ---------------------------
// Edit showing time
// ---------------------------
class EditShowingTimePayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String movieName;
    private LocalDateTime newTime;
    private LocalDateTime oldtime;

    public EditShowingTimePayload(String movieName, LocalDateTime newTime, LocalDateTime oldtime) {
        this.movieName = movieName;
        this.newTime = newTime;
        this.oldtime = oldtime;
    }

    public String getMovieName() { return movieName; }
    public LocalDateTime getNewTime() { return newTime; }
}

// ---------------------------
// Cancel a showing
// ---------------------------
class CancelShowingPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String movieName;

    public CancelShowingPayload(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieName() { return movieName; }

    public LocalDateTime getTime() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTime'");
    }
}


// Request list of movies
class RequestMoviesPayload implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
}

// Request auditorium/seating for a movie
class RequestAuditoriumPayload implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String movieName;

    public RequestAuditoriumPayload(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieName() { return movieName; }
}
// ---------------------------
// Create a venue / showing
// ---------------------------
class CreateVenuePayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String venueName;
    private int rows;
    private int cols;
    private String showingName;
    private LocalDateTime showingTime;
    private double defaultSeatPrice;

    public CreateVenuePayload(String venueName, int rows, int cols, String showingName, LocalDateTime showingTime, double defaultSeatPrice) {
        this.venueName = venueName;
        this.rows = rows;
        this.cols = cols;
        this.showingName = showingName;
        this.showingTime = showingTime;
        this.defaultSeatPrice = defaultSeatPrice;
    }

    public String getVenueName() { return venueName; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public String getShowingName() { return showingName; }
    public LocalDateTime getShowingTime() { return showingTime; }
    public double getDefaultSeatPrice() { return defaultSeatPrice; }
}
