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
import java.util.ArrayList;

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
public class RunLocalTest {
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
        public void testAdultOnlyEventToString() {
            AdultOnlyEvent myEvent = new AdultOnlyEvent(5.099, 18, 99, 5);
            String actualToString = myEvent.toString();
            String expectedToString = "AdultOnlyEvent\n" +
                    "TicketPrice: 5.10\n" +
                    "LowerAgeLimit: 18\n" +
                    "UpperAgeLimit: 99\n" +
                    "TicketsRemaining: 5";
            assertEquals("Ensure your toString() method in AdultOnlyEvent.java returns the correct value!",
                    expectedToString, actualToString);
        }
    }
}