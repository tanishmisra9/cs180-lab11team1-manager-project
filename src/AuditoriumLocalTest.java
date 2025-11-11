package src;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import src.Auditorium;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;



/**
 * The JUnit LocalTest for Auditorium.
 * 
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author Logan Dalton
 * @version Nov 10, 2025
 */

public class AuditoriumLocalTest {

    private Auditorium rectAud; // A rectangular auditorium
    private Auditorium jaggedAud; // A jagged auditorium
    //private final double DEFAULT_PRICE = 10.0;
    private final double floatingComparison = 0.001; // for floating point comparisons (DELTA)

    @BeforeEach
    public void setup() {
        // A 3x4 auditorium
        rectAud = new Auditorium(3, 4);

        // A jagged auditorium with row lengths 2, 4, and 3
        int[] rowLengths = {2, 4, 3};
        jaggedAud = new Auditorium(rowLengths);
    }

    //---CONSTRUCTOR TESTS---//

    @Test
    public void testConstructor_RowsCols() {
        Auditorium aud = new Auditorium(5, 7);
        assertEquals(5, aud.getRowNumber());
        assertEquals(7, aud.getColumnNumber());
        assertEquals("empty", aud.getSeats()[0][0]);
        assertEquals(0.0, aud.getSeatPrices()[0][0], floatingComparison);
    }

    @Test
    public void testConstructor_RowLengths() {
        assertEquals(3, jaggedAud.getRowNumber());
        assertEquals(4, jaggedAud.getColumnNumber());
        assertEquals(2, jaggedAud.getSeats()[0].length);
        assertEquals(4, jaggedAud.getSeats()[1].length);
        assertEquals(3, jaggedAud.getSeats()[2].length);
        assertEquals("empty", jaggedAud.getSeats()[0][0]);
        assertEquals(0.0, jaggedAud.getSeatPrices()[1][3], floatingComparison);
    }

    @Test
    public void testConstructor_StringArray() {
        String[][] layout = {{"", ""}, {"", "", ""}};
        Auditorium aud = new Auditorium(layout);
        assertEquals(2, aud.getRowNumber());
        assertEquals(3, aud.getColumnNumber());
        assertEquals(2, aud.getSeats()[0].length);
        assertEquals(3, aud.getSeats()[1].length);
        // Constructor initializes seats without copying layout
        assertEquals("empty", aud.getSeats()[0][0]);
        assertEquals(0.0, aud.getSeatPrices()[0][0], floatingComparison);
    }

    @Test
    public void testConstructor_RowsColsPrices() {
        Auditorium aud = new Auditorium(2, 2, 9.99);
        assertEquals(2, aud.getRowNumber());
        assertEquals(2, aud.getColumnNumber());
        assertEquals(9.99, aud.getSeatPrices()[0][0], floatingComparison);
        assertEquals(9.99, aud.getSeatPrices()[1][1], floatingComparison);
    }

    @Test
    public void testConstructor_RowLengthsPrices() {
        int[] rowLengths = {1, 3};
        Auditorium aud = new Auditorium(rowLengths, 12.50);
        assertEquals(2, aud.getRowNumber());
        assertEquals(3, aud.getColumnNumber());
        assertEquals(1, aud.getSeatPrices()[0].length);
        assertEquals(3, aud.getSeatPrices()[1].length);
        assertEquals(12.50, aud.getSeatPrices()[0][0], floatingComparison);
        assertEquals(12.50, aud.getSeatPrices()[1][2], floatingComparison);
    }

    @Test
    public void testConstructor_StringArrayPrices() {
        String[][] layout = {{"", ""}, {"", "", ""}};
        Auditorium aud = new Auditorium(layout, 7.50);
        assertEquals(2, aud.getRowNumber());
        assertEquals(3, aud.getColumnNumber());
        assertEquals(7.50, aud.getSeatPrices()[0][1], floatingComparison);
        assertEquals(7.50, aud.getSeatPrices()[1][2], floatingComparison);
        assertEquals("empty", aud.getSeats()[0][0]);
    }

    //---Getter/Setter Tests---//

    @Test
    public void testGetSetShowingName() {
        rectAud.setShowingName("The Matrix");
        assertEquals("The Matrix", rectAud.getShowingName());
    }

    @Test
    public void testGetRowNumber() {
        assertEquals(3, rectAud.getRowNumber());
        assertEquals(3, jaggedAud.getRowNumber());
    }

    @Test
    public void testGetColumnNumber() {
        assertEquals(4, rectAud.getColumnNumber());
        assertEquals(4, jaggedAud.getColumnNumber());
    }

    @Test
    public void testGetSeats_ReturnsNewCopy() {
        String[][] seats = rectAud.getSeats();
        seats[0][0] = "MODIFIED";
        assertEquals("empty", rectAud.getSeats()[0][0]);
    }

    @Test
    public void testGetSeatPrices_ReturnsNewCopy() {
        double[][] prices = rectAud.getSeatPrices();
        prices[0][0] = 99.99;
        assertEquals(0.0, rectAud.getSeatPrices()[0][0], floatingComparison);
    }

    @Test
    public void testGetSetDate() {
        LocalDateTime testDate = LocalDateTime.of(2025, 1, 1, 10, 30);
        rectAud.setDate(testDate);
        assertEquals(testDate, rectAud.getDate());
    }

    @Test
    public void testGetSetShowingDate() {
        LocalDateTime showTime = LocalDateTime.of(2025, 12, 25, 20, 0);
        rectAud.setShowingTime(showTime);
        assertEquals(showTime, rectAud.getShowingTime());
    }

    @Test
    public void testSetShowingDate_Int() {
        LocalDateTime showDate = LocalDateTime.of(2025, 10, 31, 19, 0);
        rectAud.setShowingDate(2025, 10, 31, 19, 0);
        assertEquals(showDate, rectAud.getShowingTime());
    }

    @Test
    public void testSetSeats_StringArray() {
        String[][] newLayout = {{"", ""}, {""}, {"", "", ""}};
        rectAud.setSeats(newLayout);

        assertEquals(3, rectAud.getRowNumber());
        assertEquals(3, rectAud.getColumnNumber());
        assertEquals(2, rectAud.getSeats()[0].length);
        assertEquals(1, rectAud.getSeats()[1].length);
        assertEquals(3, rectAud.getSeats()[2].length);
        assertEquals("empty", rectAud.getSeats()[0][0]); // check it reset
        assertEquals(0.0, rectAud.getSeatPrices()[0][0], floatingComparison); // check if prices reset
    }

    @Test
    public void testSetSeatPrices_DoubleArray() {
        double[][] newPrices = {{1.0, 2.0}, {3.0, 4.0, 5.0}};
        rectAud.setSeatPrices(newPrices);

        assertEquals(2, rectAud.getRowNumber());
        assertEquals(3, rectAud.getColumnNumber());
        assertEquals(2, rectAud.getSeatPrices()[0].length);
        assertEquals(3, rectAud.getSeatPrices()[1].length);
        assertEquals(1.0, rectAud.getSeatPrices()[0][0], floatingComparison);
        assertEquals(5.0, rectAud.getSeatPrices()[1][2], floatingComparison);
        assertEquals("empty", rectAud.getSeats()[0][0]); //check seats reset
    }

    @Test
    public void testSetSeats_RowCol() {
        rectAud.setSeats(5, 5);
        assertEquals(5, rectAud.getRowNumber());
        assertEquals(5, rectAud.getColumnNumber());
        assertEquals("empty", rectAud.getSeats()[4][4]);
        assertEquals(0.0, rectAud.getSeatPrices()[4][4], floatingComparison);
    }

    //---time tests---//

    @Test
    public void testUpdateTime_LocalDateTime() {
        LocalDateTime testDate = LocalDateTime.of(2025, 1, 1, 10, 30);
        rectAud.updateTime(testDate);
        assertEquals(testDate, rectAud.getDate());
    }

    @Test
    public void testUpdateTime_Int() {
        LocalDateTime testDate = LocalDateTime.of(2024, 5, 10, 15, 0);
        rectAud.updateTime(2024, 5, 10, 15, 0);
        assertEquals(testDate, rectAud.getDate());
    }

    @Test
    public void testUpdateShowingTime_Success() {
        LocalDateTime currentDate = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime newShowTime = LocalDateTime.of(2025, 1, 1, 13, 0);
        rectAud.updateTime(currentDate);
        rectAud.updateShowingTime(newShowTime);
        assertEquals(newShowTime, rectAud.getShowingTime());
    }

    @Test
    public void testUpdateShowingTime_Fail_BeforeCurrent() {
        LocalDateTime currentDate = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime newShowTime = LocalDateTime.of(2025, 1, 1, 11, 0); // Before current
        rectAud.updateTime(currentDate);
        rectAud.setShowingTime(null); //no showing date

        rectAud.updateShowingTime(newShowTime);
        assertNull(rectAud.getShowingTime()); // expected to fail to update
    }

    @Test
    public void testUpdateShowingTime_Int_Success() {
        LocalDateTime currentDate = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime newShowTime = LocalDateTime.of(2025, 1, 2, 10, 0);
        rectAud.updateTime(currentDate);

        rectAud.updateShowingTime(2025, 1, 2, 10, 0);
        assertEquals(newShowTime, rectAud.getShowingTime());
    }

    //--price changes tests---//

    @Test
    public void testSetPrice() {
        rectAud.setPrice(1, 2, 15.50);
        assertEquals(15.50, rectAud.getSeatPrices()[1][2], floatingComparison);
    }

    @Test
    public void testSetPrice_Negative() {
        rectAud.setPrice(1, 2, 15.50);
        rectAud.setPrice(1, 2, -5.00); // expected to fail
        assertEquals(15.50, rectAud.getSeatPrices()[1][2], floatingComparison); // price unchanged
    }

    @Test
    public void testSetPrice_OutOfBounds() {
        // should warn with no exception
        rectAud.setPrice(99, 99, 20.0);
        //no assertion cause checking for crash
    }

    @Test
    public void testSetAllPrices() {
        jaggedAud.setAllPrices(12.00);
        assertEquals(12.00, jaggedAud.getSeatPrices()[0][1], floatingComparison);
        assertEquals(12.00, jaggedAud.getSeatPrices()[1][3], floatingComparison);
        assertEquals(12.00, jaggedAud.getSeatPrices()[2][2], floatingComparison);
    }

    @Test
    public void testIncreaseAllPrices() {
        rectAud.setAllPrices(10.00);
        rectAud.increaseAllPrices(2.50);
        assertEquals(12.50, rectAud.getSeatPrices()[0][0], floatingComparison);
        assertEquals(12.50, rectAud.getSeatPrices()[2][3], floatingComparison);
    }

    @Test
    public void testMultiplyAllPrices() {
        rectAud.setAllPrices(10.00);
        rectAud.multiplyAllPrices(1.5);
        assertEquals(15.00, rectAud.getSeatPrices()[0][0], floatingComparison);
        assertEquals(15.00, rectAud.getSeatPrices()[2][3], floatingComparison);
    }

    @Test
    public void testSetRowPrices() {
        rectAud.setAllPrices(5.00);
        rectAud.setRowPrices(1, 20.00);
        assertEquals(5.00, rectAud.getSeatPrices()[0][0], floatingComparison);
        assertEquals(20.00, rectAud.getSeatPrices()[1][0], floatingComparison);
        assertEquals(20.00, rectAud.getSeatPrices()[1][3], floatingComparison);
        assertEquals(5.00, rectAud.getSeatPrices()[2][0], floatingComparison);
    }

    @Test
    public void testSetColPrices() {
        rectAud.setAllPrices(5.00);
        rectAud.setColPrices(2, 25.00);
        assertEquals(5.00, rectAud.getSeatPrices()[0][1], floatingComparison);
        assertEquals(25.00, rectAud.getSeatPrices()[0][2], floatingComparison);
        assertEquals(25.00, rectAud.getSeatPrices()[1][2], floatingComparison);
        assertEquals(25.00, rectAud.getSeatPrices()[2][2], floatingComparison);
        assertEquals(5.00, rectAud.getSeatPrices()[0][3], floatingComparison);
    }

    @Test
    public void testSetColPrices_Jagged() {
        jaggedAud.setAllPrices(5.00); // {2, 4, 3}
        jaggedAud.setColPrices(2, 30.00);

        // Row 0: 2 cols (0, 1)
        // so col 2 is out of bounds
        assertEquals(5.00, jaggedAud.getSeatPrices()[0][1], floatingComparison);

        // Row 1: 4 cols (0, 1, 2, 3)
        assertEquals(30.00, jaggedAud.getSeatPrices()[1][2], floatingComparison);

        // Row 2: 3 cols (0, 1, 2)
        assertEquals(30.00, jaggedAud.getSeatPrices()[2][2], floatingComparison);
    }

    @Test
    public void testIncreaseRowPrices() {
        rectAud.setRowPrices(1, 10.00);
        rectAud.increaseRowPrices(1, 2.00);
        assertEquals(0.00, rectAud.getSeatPrices()[0][0], floatingComparison);
        assertEquals(12.00, rectAud.getSeatPrices()[1][0], floatingComparison);
        assertEquals(12.00, rectAud.getSeatPrices()[1][3], floatingComparison);
    }

    @Test
    public void testMultiplyRowPrices() {
        rectAud.setRowPrices(1, 10.00);
        rectAud.multiplyRowPrices(1, 3.00);
        assertEquals(0.00, rectAud.getSeatPrices()[0][0], floatingComparison);
        assertEquals(30.00, rectAud.getSeatPrices()[1][0], floatingComparison);
        assertEquals(30.00, rectAud.getSeatPrices()[1][3], floatingComparison);
    }

    @Test
    public void testIncreaseColPrices() {
        rectAud.setColPrices(1, 10.00);
        rectAud.increaseColPrices(1, 5.00);
        assertEquals(0.00, rectAud.getSeatPrices()[0][0], floatingComparison);
        assertEquals(15.00, rectAud.getSeatPrices()[0][1], floatingComparison);
        assertEquals(15.00, rectAud.getSeatPrices()[2][1], floatingComparison);
    }

    @Test
    public void testMultiplyColPrices() {
        rectAud.setColPrices(1, 10.00);
        rectAud.multiplyColPrices(1, 2.00);
        assertEquals(0.00, rectAud.getSeatPrices()[0][0], floatingComparison);
        assertEquals(20.00, rectAud.getSeatPrices()[0][1], floatingComparison);
        assertEquals(20.00, rectAud.getSeatPrices()[2][1], floatingComparison);
    }

    //---Reservation/Seat Tests---//

    @Test
    public void testIsValidSeat() {
        assertTrue(rectAud.isValidSeat(0, 0));
        assertTrue(rectAud.isValidSeat(2, 3));
        assertFalse(rectAud.isValidSeat(-1, 0)); // negative row
        assertFalse(rectAud.isValidSeat(0, -1)); // negative col
        assertFalse(rectAud.isValidSeat(3, 0)); // out of bounds row
        assertFalse(rectAud.isValidSeat(0, 4)); // out of bounds col
    }

    @Test
    public void testIsValidSeat_Jagged() {
        assertTrue(jaggedAud.isValidSeat(0, 1)); // row 0 len 2
        assertFalse(jaggedAud.isValidSeat(0, 2)); // row 0 (out of bounds)
        assertTrue(jaggedAud.isValidSeat(1, 3)); // row 1 len 4
        assertFalse(jaggedAud.isValidSeat(1, 4)); // row 1 (out of bounds)
    }

    @Test
    public void testCheckSeat_Empty() {
        assertTrue(rectAud.checkSeat(1, 1));
    }

    @Test
    public void testCheckSeat_OutOfBounds() {
        assertFalse(rectAud.checkSeat(99, 99));
    }

    @Test
    public void testSetReservation_Success() {
        assertTrue(rectAud.setReservation("Tanish", 1, 1));
        assertEquals("Tanish", rectAud.getSeats()[1][1]);
    }

    @Test
    public void testSetReservation_Fail_Taken() {
        rectAud.setReservation("Tanish", 1, 1);
        assertFalse(rectAud.setReservation("Noli", 1, 1));
        assertEquals("Tanish", rectAud.getSeats()[1][1]); // Still Alice
    }

    @Test
    public void testSetReservation_Fail_OutOfBounds() {
        assertFalse(rectAud.setReservation("Jim", 99, 99));
    }

    @Test
    public void testCheckSeat_AfterReservation() {
        rectAud.setReservation("Tanish", 1, 1);
        assertFalse(rectAud.checkSeat(1, 1));
    }
}
