import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThirdLargeViaLpFreeRuleTest {

    private static final BigDecimal LARGE_LP_PRICE = new BigDecimal("6.90");

    private static Transaction largeLp(String date) {
        return Transaction.fromLine(date + " L LP");
    }

    @Test
    void onlyTheThirdLargeLpInAMonthIsFree() {
        ThirdLargeViaLpFreeRule rule = new ThirdLargeViaLpFreeRule(); // fresh state per test
        assertMoneyEquals("6.90", rule.apply(largeLp("2015-02-01"), LARGE_LP_PRICE)); // 1st
        assertMoneyEquals("6.90", rule.apply(largeLp("2015-02-02"), LARGE_LP_PRICE)); // 2nd
        assertMoneyEquals("0.00", rule.apply(largeLp("2015-02-03"), LARGE_LP_PRICE)); // 3rd -> free
        assertMoneyEquals("6.90", rule.apply(largeLp("2015-02-04"), LARGE_LP_PRICE)); // 4th -> not free
    }

    @Test
    void theCountResetsEachCalendarMonth() {
        ThirdLargeViaLpFreeRule rule = new ThirdLargeViaLpFreeRule();
        rule.apply(largeLp("2015-02-01"), LARGE_LP_PRICE);
        rule.apply(largeLp("2015-02-02"), LARGE_LP_PRICE);
        rule.apply(largeLp("2015-02-03"), LARGE_LP_PRICE); // third of February
        // March starts fresh -> first two are NOT free
        assertMoneyEquals("6.90", rule.apply(largeLp("2015-03-01"), LARGE_LP_PRICE));
        assertMoneyEquals("6.90", rule.apply(largeLp("2015-03-02"), LARGE_LP_PRICE));
    }

    @Test
    void otherShipmentsAreUnaffectedAndDoNotCount() {
        ThirdLargeViaLpFreeRule rule = new ThirdLargeViaLpFreeRule();
        // large via MR and medium via LP: returned unchanged, and must NOT advance the LP-large count
        assertMoneyEquals("4.00", rule.apply(Transaction.fromLine("2015-02-01 L MR"), new BigDecimal("4.00")));
        assertMoneyEquals("4.90", rule.apply(Transaction.fromLine("2015-02-02 M LP"), new BigDecimal("4.90")));
        // two genuine large-LP shipments are still only the 1st and 2nd -> neither is free
        assertMoneyEquals("6.90", rule.apply(largeLp("2015-02-03"), LARGE_LP_PRICE));
        assertMoneyEquals("6.90", rule.apply(largeLp("2015-02-04"), LARGE_LP_PRICE));
    }

    private static void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }
}
