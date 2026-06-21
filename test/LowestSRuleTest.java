import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LowestSRuleTest {

    private final LowestSRule rule = new LowestSRule(); // stateless, safe to reuse

    @Test
    void smallMatchesTheLowestSmallPrice() {
        // MR small base is 2.00, but it should drop to the lowest small price (LP's 1.50)
        assertMoneyEquals("1.50",
                rule.apply(Transaction.fromLine("2015-02-01 S MR"), new BigDecimal("2.00")));
    }

    @Test
    void smallAlreadyAtLowestStaysTheSame() {
        assertMoneyEquals("1.50",
                rule.apply(Transaction.fromLine("2015-02-01 S LP"), new BigDecimal("1.50")));
    }

    @Test
    void nonSmallIsLeftUnchanged() {
        assertMoneyEquals("4.90",
                rule.apply(Transaction.fromLine("2015-02-01 M LP"), new BigDecimal("4.90")));
        assertMoneyEquals("6.90",
                rule.apply(Transaction.fromLine("2015-02-01 L LP"), new BigDecimal("6.90")));
    }

    private static void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }
}
