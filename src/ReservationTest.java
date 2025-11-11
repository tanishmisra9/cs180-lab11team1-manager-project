package src;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.experimental.runners.Enclosed;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.io.*;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * A framework to run public test cases.
 *
 * <p>Purdue University -- CS18000 -- </p>
 *
 * @author Ved Joshi, [add your name here]
 * @version Nov 10, 2025
 */

@RunWith(Enclosed.class)
public class ReservationTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Test ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }

    /**
     * A set of public test cases.
     *
     * <p>Purdue University -- CS18000 -- </p>
     *
     * @author Ved Joshi, [add your name here]
     * @version Nov 10, 2025
     */
    public static class TestCase {
        private final PrintStream originalOutput = System.out;
        private final InputStream originalSysin = System.in;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayInputStream testIn;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOut;

        @Before
        public void outputStart() {
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut));
        }

        @After
        public void restoreInputAndOutput() {
            System.setIn(originalSysin);
            System.setOut(originalOutput);
        }

        private String getOutput() {
            return testOut.toString();
        }

        @SuppressWarnings("SameParameterValue")
        private void receiveInput(String str) {
            testIn = new ByteArrayInputStream(str.getBytes());
            System.setIn(testIn);
        }


        //Sample test from one of our earlier homeworks. 
        @Test(timeout = 1000)
        public void testConstructor() {
            String user = null;
            String movie = "Jurassic Park";
            LocalDateTime date = LocalDateTime.now();
            int row = 1;
            int seat = 1;

            //null user
            try {
                BasicReservation testReservation = new BasicReservation(user, movie, date, row, seat);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

            //null movie
            user = "Taewoo Jung";
            movie = null;
            date = LocalDateTime.now();
            row = 1;
            seat = 1;

            try {
                BasicReservation testReservation = new BasicReservation(user, movie, date, row, seat);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

            //null date
            user = "Taewoo Jung";
            movie = "Jurassic Park";
            date = null;
            row = 1;
            seat = 1;

            try {
                BasicReservation testReservation = new BasicReservation(user, movie, date, row, seat);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

        }
    }
}