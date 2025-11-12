package src;

import org.junit.jupiter.api.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
// pushed changes that were taking forever
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllUserClassesTest {

    private BasicUser user;
    private CreditCard card;
    private UserAccountManager manager;

    @BeforeEach
    void setup() {
        user = new BasicUser("alice", "password123", false, UserType.REGULAR);
        card = new CreditCard("Alice Smith", "1234567812345678", "12/30", "123");
        user.setCreditCard(card);
        manager = new UserAccountManager();
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

    @Test @Order(5)
    void testAddReservationNoCreditCardFails() {
        BasicUser noCardUser = new BasicUser("bob", "secret", false,  UserType.REGULAR);
        boolean success = noCardUser.addReservation(
                "Matrix",
                LocalDateTime.now(), 1, 1, 2, 12.0
        );
        assertFalse(success);
    }

    @Test @Order(6)
    void testCancelReservationRefundsWrongly() {
        LocalDateTime dt = LocalDateTime.of(2025, 11, 10, 19, 0);
        user.addReservation("Inception", dt, 2, 5, 3, 20.0);

        boolean cancel = user.cancelReservation("Inception", "8PM", dt.toLocalDate(), 2, 20.0);

        assertFalse(cancel);
        assertFalse(user.getTransactionHistory().stream().anyMatch(s -> s.contains("Refunded")));
        assertEquals(3, user.getReservations().size()); // one reservation still active
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
        BasicUser noCard = new BasicUser("charlie", "abc", false,   UserType.REGULAR);
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
    // USERACCOUNTMANAGER TESTS
    // -----------------------------

    @Test @Order(12)
    void testCreateAndCountUsers() {
        assertTrue(manager.createUser("alice", "pw", false, UserType.REGULAR));
        assertEquals(1, manager.getUserCount());
        assertFalse(manager.createUser("alice", "pw", false, UserType.REGULAR)); // duplicate
    }

    @Test @Order(13)
    void testDeleteUser() {
        manager.createUser("bob", "pw", false, UserType.REGULAR);
        assertTrue(manager.deleteUser("bob"));
        assertFalse(manager.deleteUser("bob"));
    }

    @Test @Order(14)
    void testChangePassword() {
        manager.createUser("chris", "pw", false, UserType.REGULAR);
        assertFalse(manager.changePassword("notexist", "pw", "newpw"));
        assertFalse(manager.changePassword("chris", "wrongpw", "newpw"));
    }

    @Test @Order(15)
    void testUpgradeUserInManager() {
        manager.createUser("diana", "pw", false, UserType.REGULAR);
        // Note: credit card isn’t stored inside manager, so upgrade may print "failed"
        assertFalse(manager.upgradeUser("diana", UserType.VIP, 50.0));
    }

    @Test @Order(16)
    void testLoginAndFail() {
        manager.createUser("eva", "pw", true, UserType.REGULAR);
        assertFalse(manager.login("eva", "wrongpw"));
        assertFalse(manager.login("evadoesnotexist", "pw"));
//        assertFalse(manager.login("eva", "wrongpw"));
//        assertFalse(manager.login("notexist", "pw"));
    }

    @Test @Order(17)
    void testSaveAndLoadUsers() {
        manager.createUser("fred", "pw", false, UserType.PREMIUM);

        String filename = "test_users.txt";
        manager.saveToFile(filename);

        File file = new File(filename);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        UserAccountManager manager2 = new UserAccountManager();
        manager2.boot(filename);
        assertTrue(manager2.getUserCount() >= 1);

        file.delete(); // clean up
    }
}
