package src;

import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for BasicUser and CreditCard classes.
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @version Nov 19, 2025
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllUserClassesTest {

    private BasicUser user;
    private CreditCard card;
    private ReservationDatabase db;

    @BeforeEach
    void setup() {
        user = new BasicUser("alice", "password123", false, UserType.REGULAR);
        card = new CreditCard("Alice Smith", "1234567812345678", "12/30", "123");
        user.setCreditCard(card);
        db = new ReservationDatabase();
    }

    // -----------------------------
    // BASICUSER TESTS
    // -----------------------------

    @Test @Order(1)
    void testPasswordHashing() {
        String hashed = user.getPassword();
        assertNotEquals("password123", hashed);
        assertTrue(hashed.matches("[a-f0-9]{64}"));
    }

    @Test @Order(2)
    void testSetPasswordChangesHash() {
        String oldHash = user.getPassword();
        user.setPassword("newPass!");
        assertNotEquals(oldHash, user.getPassword());
    }

    @Test @Order(3)
    void testGetPriceMultiplier() {
        assertEquals(1.0, user.getPriceMultiplier());
        user.upgradeUser(UserType.PREMIUM, 10.0);
        assertEquals(0.9, user.getPriceMultiplier());
        user.upgradeUser(UserType.VIP, 10.0);
        assertEquals(0.75, user.getPriceMultiplier());
    }

    
    @Test @Order(4)
    void testAddReservationSuccess() {
        boolean success = user.addReservation(
                "Interstellar",
                LocalDateTime.of(2025, 11, 10, 19, 0),
                3, 10, 2, 15.0
        );

        assertTrue(success);
        List<BasicReservation> reservations = user.getReservations();
        assertEquals(2, reservations.size());
        assertEquals("Interstellar", reservations.get(0).getMovie());
        assertTrue(user.getTransactionHistory().get(0).contains("Charged $"));
    }
        

    /*
    @Test @Order(4)
void testAddReservationSuccess_VoidMethod() {
    
    user.addReservation(
            "Interstellar",
            LocalDateTime.of(2025, 11, 10, 19, 0),
            3, 10, 2, 15.0
    );
    List<BasicReservation> reservations = user.getReservations();
    
    assertEquals(2, reservations.size(), "Reservation count should be 2, not 1");
    
    assertEquals("Interstellar", reservations.get(1).getShowtime(), 
                 "The movie title of the newly added reservation is incorrect.");

    assertTrue(user.getTransactionHistory().get(user.getTransactionHistory().size() - 1).contains("Charged $15.0"),
               "Transaction history should contain the correct 'Charged' entry for $15.0.");
}
               */

    
    @Test @Order(5)
    void testAddReservationNoCreditCardFails() {
        BasicUser noCardUser = new BasicUser("bob", "secret", false, UserType.REGULAR);
        boolean success = noCardUser.addReservation(
                "Matrix",
                LocalDateTime.now(), 1, 1, 2, 12.0
        );
        assertFalse(success);
    }
        
       
    /* 
    @Test @Order(5)
    void testAddReservationNoCreditCardFails() {
        BasicUser noCardUser = new BasicUser("bob", "secret", false, UserType.REGULAR);
        int initialReservationCount = noCardUser.getReservations().size();
        noCardUser.addReservation(
            "Matrix",
            LocalDateTime.now(), 1, 1, 2, 12.0
        );
        int finalReservationCount = noCardUser.getReservations().size();
        assertEquals(initialReservationCount, finalReservationCount,
            "Reservation count should not change when a user has no credit card.");
    }
            */



    @Test @Order(6)
    void testCancelReservationFailsWithEmptyShowtime() {
        LocalDateTime dt = LocalDateTime.of(2025, 11, 10, 19, 0);
        user.addReservation("Inception", dt, 2, 5, 3, 20.0);

        // Cancel will fail because BasicReservation.getShowtime() returns ""
        boolean cancel = user.cancelReservation("Inception", "", dt.toLocalDate(), 2, 20.0);
        //String movie, String showTime, LocalDateTime date, int numPeople, double reservationFee
        assertFalse(cancel);
        assertFalse(user.getTransactionHistory().stream().anyMatch(s -> s.contains("Refunded")));
        assertEquals(3, user.getReservations().size());
    }


    

    @Test @Order(7)
    void testUpgradeUser() {
        boolean success = user.upgradeUser(UserType.PREMIUM, 30.0);
        assertTrue(success);
        assertEquals(UserType.PREMIUM, user.getType());
        assertTrue(user.getTransactionHistory().get(0).contains("upgrade"));
    }

    @Test @Order(8)
    void testUpgradeUserFailsWithoutCard() {
        BasicUser noCard = new BasicUser("charlie", "abc", false, UserType.REGULAR);
        boolean result = noCard.upgradeUser(UserType.PREMIUM, 20.0);
        assertFalse(result);
    }

    @Test @Order(9)
    void testToStringContainsMaskedCard() {
        String s = user.toString();
        assertTrue(s.contains("**** **** **** 5678"));
        assertTrue(s.contains("User[alice"));
    }

    // -----------------------------
    // CREDITCARD TESTS
    // -----------------------------

    @Test @Order(10)
    void testCreditCardFields() {
        assertEquals("Alice Smith", card.getCardHolderName());
        assertEquals("12/30", card.getExpiryDate());
        assertTrue(card.toString().contains("**** **** **** 5678"));
    }

    @Test @Order(11)
    void testMaskedNumberFormat() {
        String masked = card.getMaskedNumber();
        assertEquals("**** **** **** 5678", masked);
    }

    // -----------------------------
    // RESERVATIONDATABASE TESTS
    // -----------------------------

    @Test @Order(12)
    void testCreateAndCountUsers() {
        BasicUser user1 = new BasicUser("alice", "pw", false, UserType.REGULAR);
        db.addUser(user1);
        assertEquals(1, db.getUsers().size());

        // Adding same username creates duplicate (no duplicate check in addUser)
        BasicUser user2 = new BasicUser("alice", "pw", false, UserType.REGULAR);
        db.addUser(user2);
        assertEquals(2, db.getUsers().size());
    }

    @Test @Order(13)
    void testGetUserByUsername() {
        BasicUser user1 = new BasicUser("bob", "pw", false, UserType.REGULAR);
        db.addUser(user1);

        BasicUser found = db.getUserByUsername("bob");
        assertNotNull(found);
        assertEquals("bob", found.getUsername());

        BasicUser notFound = db.getUserByUsername("nonexistent");
        assertNull(notFound);
    }

    @Test @Order(14)
    void testAddAuditorium() {
        LocalDateTime showTime = LocalDateTime.of(2025, 11, 20, 19, 0);
        Auditorium aud = new Auditorium(10, 12, 15.0, "Inception", showTime);
        db.addAuditorium(aud);

        assertEquals(1, db.getAuditoriums().size());
        assertEquals("Inception", db.getAuditoriums().get(0).getShowingName());
    }

    @Test @Order(15)
    void testReserveSuccess() {
        // Setup user and auditorium
        db.addUser(user);
        LocalDateTime showTime = LocalDateTime.of(2025, 11, 20, 19, 0);
        Auditorium aud = new Auditorium(10, 12, 15.0, "Inception", showTime);
        db.addAuditorium(aud);

        // Make reservation
        BasicReservation r = new BasicReservation(user.getUsername(), "Inception", showTime, 5, 5);
        boolean success = db.reserve(user, r);

        assertTrue(success);
        assertEquals(1, user.getReservations().size());
    }

    @Test @Order(16)
    void testReserveFails() {
        // No auditorium added
        db.addUser(user);
        LocalDateTime showTime = LocalDateTime.of(2025, 11, 20, 19, 0);
        BasicReservation r = new BasicReservation(user.getUsername(), "Inception", showTime, 5, 5);

        boolean success = db.reserve(user, r);
        assertFalse(success);
    }

    @Test @Order(17)
    void testGetReservations() {
        db.addUser(user);
        LocalDateTime showTime = LocalDateTime.of(2025, 11, 20, 19, 0);
        Auditorium aud = new Auditorium(10, 12, 15.0, "Inception", showTime);
        db.addAuditorium(aud);

        BasicReservation r1 = new BasicReservation(user.getUsername(), "Inception", showTime, 1, 1);
        BasicReservation r2 = new BasicReservation(user.getUsername(), "Inception", showTime, 1, 2);

        db.reserve(user, r1);
        db.reserve(user, r2);

        List<BasicReservation> reservations = db.getReservations(user);
        assertEquals(2, reservations.size());
    }
}
