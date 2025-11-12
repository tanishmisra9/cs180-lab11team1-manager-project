package src;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;	

public class ReservationDatabaseTest {
	private ReservationDatabase db;
	private BasicUser user;
	private Auditorium auditorium;

	@Before
	public void setup() {
		db = new ReservationDatabase();
		user = new BasicUser("Test User", "password", false, UserType.REGULAR);
        CreditCard card;
        card = new CreditCard("Alice Smith", "1234567812345678", "12/30", "123");
        user.setCreditCard(card);
        db.addUser(user);

		LocalDateTime showingtime = LocalDateTime.of(2025, 1, 1, 19, 0);
		auditorium  = new Auditorium(6, 7, 5.0, "Look Back", showingtime);
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
		assertEquals(r, reservations.get(0));
	}

	@Test
	public void testReserveInvalidSeat() {
		BasicReservation r = new BasicReservation(user.getUsername(), "Look Back", LocalDateTime.now(), 10, 10); //outside of 6x7
		boolean success = db.reserve(user, r);
		assertFalse("Reservation should fail for invalid seat", success);
	}

	@Test 
	public void testDoubleBooking() {
        LocalDateTime showTime = LocalDateTime.of(2025, 1, 1, 19, 0);
        Auditorium a = new Auditorium(2, 2, 2.0, "Look Back", showTime);
        db.addAuditorium(a);

        BasicReservation r1 = new BasicReservation(user.getUsername(), "Look Back", LocalDateTime.of(2025, 1, 1, 19, 0), 2, 2);
		BasicReservation r2 = new BasicReservation(user.getUsername(), "Look Back", LocalDateTime.of(2025, 1, 1, 19, 0), 2, 2);

		assertTrue(db.reserve(user, r1));
		assertFalse(db.reserve(user, r2));
	}

	@Test
	public void testSaveAndLoad() {

        BasicReservation r = new BasicReservation(user.getUsername(), "Look Back", LocalDateTime.of(2025, 1, 1, 19, 0), 1, 1);
        db.reserve(user, r);

		db.saveData();
		ReservationDatabase loaded = ReservationDatabase.loadDatabase();

        	List<BasicReservation> loadedRes = loaded.getReservations(user);
        	assertEquals(1, loadedRes.size());
        	assertEquals(r, loadedRes.get(0));
	}


}
