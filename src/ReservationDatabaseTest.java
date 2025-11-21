package src;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JUnit tests for ReservationDatabase class.
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @version Nov 19, 2025
 */
public class ReservationDatabaseTest {
    private ReservationDatabase db;
    private BasicUser user;
    private Auditorium auditorium;

    @Before
    public void setup() {
        db = new ReservationDatabase();
        user = new BasicUser("TestUser", "password", false, UserType.REGULAR);
        CreditCard card = new CreditCard("Alice Smith", "1234567812345678", "12/30", "123");
        user.setCreditCard(card);
        db.addUser(user);

        LocalDateTime showingtime = LocalDateTime.of(2025, 1, 1, 19, 0);
        auditorium = new Auditorium(6, 7, 5.0, "Look Back", showingtime);
        db.addAuditorium(auditorium);
    }

    @Test
    public void testReserveSeatSuccess() {
        LocalDateTime showTime = LocalDateTime.of(2025, 1, 1, 19, 0);
        BasicReservation r = new BasicReservation(user.getUsername(), "Look Back", showTime, 2, 3);
        boolean success = db.reserve(user, r);

        assertTrue("Reservation should succeed", success);
        assertFalse("Seat is taken now", auditorium.checkSeat(1, 2));

        List<BasicReservation> reservations = db.getReservations(user);
        assertEquals(1, reservations.size());
        assertEquals(r.getMovie(), reservations.get(0).getMovie());
        assertEquals(r.getRow(), reservations.get(0).getRow());
        assertEquals(r.getSeat(), reservations.get(0).getSeat());
    }

    @Test
    public void testReserveInvalidSeat() {
        BasicReservation r = new BasicReservation(user.getUsername(), "Look Back",
                LocalDateTime.of(2025, 1, 1, 19, 0), 10, 10); // outside of 6x7
        boolean success = db.reserve(user, r);
        assertFalse("Reservation should fail for invalid seat", success);
    }

    @Test
    public void testDoubleBooking() {
        LocalDateTime showTime = LocalDateTime.of(2025, 1, 1, 19, 0);

        BasicReservation r1 = new BasicReservation(user.getUsername(), "Look Back", showTime, 2, 2);
        BasicReservation r2 = new BasicReservation(user.getUsername(), "Look Back", showTime, 2, 2);

        assertTrue("First reservation should succeed", db.reserve(user, r1));
        assertFalse("Second reservation should fail (seat taken)", db.reserve(user, r2));
    }

    @Test
    public void testReserveWrongMovie() {
        LocalDateTime showTime = LocalDateTime.of(2025, 1, 1, 19, 0);
        BasicReservation r = new BasicReservation(user.getUsername(), "Wrong Movie", showTime, 2, 2);

        boolean success = db.reserve(user, r);
        assertFalse("Reservation should fail for non-existent movie", success);
    }

    @Test
    public void testReserveWrongTime() {
        LocalDateTime wrongTime = LocalDateTime.of(2025, 1, 1, 20, 0); // Movie is at 19:00
        BasicReservation r = new BasicReservation(user.getUsername(), "Look Back", wrongTime, 2, 2);

        boolean success = db.reserve(user, r);
        assertFalse("Reservation should fail for wrong showtime", success);
    }

    @Test
    public void testGetReservationsForUser() {
        LocalDateTime showTime = LocalDateTime.of(2025, 1, 1, 19, 0);

        BasicReservation r1 = new BasicReservation(user.getUsername(), "Look Back", showTime, 1, 1);
        BasicReservation r2 = new BasicReservation(user.getUsername(), "Look Back", showTime, 1, 2);

        db.reserve(user, r1);
        db.reserve(user, r2);

        List<BasicReservation> reservations = db.getReservations(user);
        assertEquals(2, reservations.size());
    }

    @Test
    public void testGetReservationsForNonExistentUser() {
        BasicUser otherUser = new BasicUser("Other", "pass", false, UserType.REGULAR);
        // Don't add to database

        List<BasicReservation> reservations = db.getReservations(otherUser);
        assertNull("Should return null for user not in database", reservations);
    }

    @Test
    public void testAddMultipleAuditoriums() {
        LocalDateTime showTime1 = LocalDateTime.of(2025, 1, 2, 19, 0);
        LocalDateTime showTime2 = LocalDateTime.of(2025, 1, 3, 19, 0);

        Auditorium aud1 = new Auditorium(5, 5, 10.0, "Movie A", showTime1);
        Auditorium aud2 = new Auditorium(8, 8, 12.0, "Movie B", showTime2);

        db.addAuditorium(aud1);
        db.addAuditorium(aud2);

        assertEquals(3, db.getAuditoriums().size()); // Including the one from setup
    }

    @Test
    public void testGetUserByUsername() {
        BasicUser found = db.getUserByUsername("TestUser");
        assertNotNull("User should be found", found);
        assertEquals("TestUser", found.getUsername());

        BasicUser notFound = db.getUserByUsername("NonExistent");
        assertNull("Should return null for non-existent user", notFound);
    }

    @Test
    public void testAddMultipleUsers() {
        BasicUser user2 = new BasicUser("User2", "pass", false, UserType.PREMIUM);
        BasicUser user3 = new BasicUser("User3", "pass", false, UserType.VIP);

        db.addUser(user2);
        db.addUser(user3);

        assertEquals(3, db.getUsers().size());
    }

    @Test
    public void testSaveAndLoad() {
        LocalDateTime showTime = LocalDateTime.of(2025, 1, 1, 19, 0);
        BasicReservation r = new BasicReservation(user.getUsername(), "Look Back", showTime, 1, 1);
        db.reserve(user, r);

        db.saveData();
        ReservationDatabase loaded = ReservationDatabase.loadDatabase();

        List<BasicUser> loadedUsers = loaded.getUsers();
        assertEquals(1, loadedUsers.size());

        BasicUser loadedUser = loaded.getUserByUsername("TestUser");
        assertNotNull(loadedUser);

        List<BasicReservation> loadedRes = loaded.getReservations(loadedUser);
        assertEquals(1, loadedRes.size());
        assertEquals("Look Back", loadedRes.get(0).getMovie());
    }
}