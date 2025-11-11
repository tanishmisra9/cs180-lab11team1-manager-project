import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;	

public class ReservationDatabaseTest {
	private ReservationDatabase db;
	private BasicUser user;
	private Auditorium auditorium;

	@Before
	public void setup() {
		db = new ReservationDataBase();
		user = new BasicUser("Test User", "password", false, UserType.STANDARD);

		auditorium = new Auditorium(6, 7);
		db.addAuditorium(auditorium);
	}

	@Test
	public void testReserveSeatSuccess() {
		Reservation r = new Reservation("")
	}


}
