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
		user = new BasicUser("Test User", "password", false, UserType.STANDARD);
	
		LocalDateTime now = LocalDateTime.now();
		auditorium = new Auditorium(6, 7, 5.0, "Look Back", now);
		db.addAuditorium(auditorium);
	}

	@Test
	public void testReserveSeatSuccess() {
		Reservation r = new BasicReservation(user.getUsername(), "Look Back", LocalDateTime.now(), 2, 3);
		boolean success = db.reserve(user, r);
		assertTrue("Reservation should succeed", success);

		assertFalse("Seat is taken now", auditorium.checkSeat(1, 2));

		List<Reservation> reservations = db.getReservations(user);
		assertEquals(1, reservations.size());
		assertEquals(r, reservations.get(0));
	}

	@Test
	public void testReserveInvalidSeat() {
		Reservation r = new BasicReservation(user.getUsername(), "Look Back", LocalDateTime.now(), 10, 10); //outside of 6x7
		boolean success = db.reserve(user, r);
		assertFalse("Reservation should fail for invalid seat", success);
	}

	@Test 
	public void testDoubleBooking() {
		Reservation r1 = new BasicReservation(user.getUsername(), "Look Back", LocalDateTime.now(), 2, 2);
		Reservation r2 = new BasicReservation(user.getUsername(), "Look Back", LocalDateTime.now(), 2, 2);

		assertTrue(db.reserve(user, r1));
		assertFalse(db.reserve(user, r2));
	}

	@Test
	public void testSaveAndLoad() {
		Reservation r = new BasicReservation(user.getUsername(), "Look Back", LocalDateTime.now(), 1, 1);
        	db.reserve(user, r);

		db.saveData();
		ReservationDatabase loaded = ReservationDatabase.loadDatabase();

        	List<Reservation> loadedRes = loaded.getReservations(user);
        	assertEquals(1, loadedRes.size());
        	assertEquals(r, loadedRes.get(0));
	}


}
