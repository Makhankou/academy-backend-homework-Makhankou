import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonthlyCapRuleTest {

    // A large-LP shipment "proposed free" (currentPrice 0) wants a 6.90 discount off its 6.90 base.
    private static Transaction largeLpProposedFree(String date) {
        return Transaction.fromLine(date + " L LP");
    }

    @Test
    void discountWithinBudgetIsFullyGranted() {
        MonthlyCapRule rule = new MonthlyCapRule();
        // wants 6.90 off, monthly budget is 10.00 -> fully free
        assertMoneyEquals("0.00", rule.apply(largeLpProposedFree("2015-02-01"), BigDecimal.ZERO));
    }

    @Test
    void discountIsGrantedPartiallyWhenBudgetRunsLow() {
        MonthlyCapRule rule = new MonthlyCapRule();
        rule.apply(largeLpProposedFree("2015-02-01"), BigDecimal.ZERO); // uses 6.90, leaves 3.10
        // second wants 6.90 but only 3.10 remains -> 6.90 - 3.10 = 3.80
        assertMoneyEquals("3.80", rule.apply(largeLpProposedFree("2015-02-02"), BigDecimal.ZERO));
    }

    @Test
    void noDiscountOnceBudgetIsSpent() {
        MonthlyCapRule rule = new MonthlyCapRule();
        rule.apply(largeLpProposedFree("2015-02-01"), BigDecimal.ZERO); // 6.90 used
        rule.apply(largeLpProposedFree("2015-02-02"), BigDecimal.ZERO); // 3.10 used -> 10.00 total
        // budget exhausted -> full price, no discount
        assertMoneyEquals("6.90", rule.apply(largeLpProposedFree("2015-02-03"), BigDecimal.ZERO));
    }

    @Test
    void budgetResetsEachCalendarMonth() {
        MonthlyCapRule rule = new MonthlyCapRule();
        rule.apply(largeLpProposedFree("2015-02-01"), BigDecimal.ZERO);
        rule.apply(largeLpProposedFree("2015-02-02"), BigDecimal.ZERO);
        rule.apply(largeLpProposedFree("2015-02-03"), BigDecimal.ZERO); // February budget spent
        // March is a clean slate -> fully free again
        assertMoneyEquals("0.00", rule.apply(largeLpProposedFree("2015-03-01"), BigDecimal.ZERO));
    }

    @Test
    void whenNoDiscountIsProposedNothingChanges() {
        MonthlyCapRule rule = new MonthlyCapRule();
        // currentPrice equals base price -> zero proposed discount -> unchanged, budget untouched
        assertMoneyEquals("4.90",
                rule.apply(Transaction.fromLine("2015-02-01 M LP"), new BigDecimal("4.90")));
    }

    private static void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }
}
