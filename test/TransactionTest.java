import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void parsesAValidLine() {
        Transaction t = Transaction.fromLine("2015-02-01 S MR");
        assertEquals(LocalDate.of(2015, 2, 1), t.date());
        assertEquals(Size.S, t.size());
        assertEquals(Provider.MR, t.provider());
        assertEquals("2015-02-01 S MR", t.raw());
        assertTrue(t.isValid());
    }

    @Test
    void basePriceComesFromTheTable() {
        assertMoneyEquals("2.00", Transaction.fromLine("2015-02-01 S MR").basePrice());
        assertMoneyEquals("4.90", Transaction.fromLine("2015-02-01 M LP").basePrice());
        assertMoneyEquals("4.00", Transaction.fromLine("2015-02-01 L MR").basePrice());
    }

    @Test
    void invalidDateIsNotValid() {
        assertFalse(Transaction.fromLine("2015-02-29 S MR").isValid()); // 2015 is not a leap year
        assertFalse(Transaction.fromLine("2015-13-01 S MR").isValid()); // month 13 doesn't exist
    }

    @Test
    void unknownSizeOrProviderIsNotValid() {
        assertFalse(Transaction.fromLine("2015-02-01 X MR").isValid());
        assertFalse(Transaction.fromLine("2015-02-01 S XX").isValid());
    }

    @Test
    void tooFewTokensIsNotValid() {
        assertFalse(Transaction.fromLine("2015-02-01 S").isValid()); // missing provider
    }

    @Test
    void tooManyTokensIsNotValid() {
        assertFalse(Transaction.fromLine("2015-02-01 S MR EXTRA").isValid());
    }

    @Test
    void rawIsPreservedEvenForInvalidLines() {
        Transaction t = Transaction.fromLine("2015-02-29 CUSPS");
        assertFalse(t.isValid());
        assertEquals("2015-02-29 CUSPS", t.raw()); // original line kept for the "Ignored" output
    }

    private static void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }
}
