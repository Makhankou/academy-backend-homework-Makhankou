import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceCalculatorTest {

    // NOTE: these call CalculateFinalPriceFor to match the current code. If you apply the
    // recommended lowercase rename (calculateFinalPriceFor), update the calls below too.

    @Test
    void appliesAllRulesAcrossASequence() {
        PriceCalculator calc = new PriceCalculator();
        assertMoneyEquals("1.50", calc.calculateFinalPriceFor(Transaction.fromLine("2015-02-01 S MR"))); // reduced to lowest small
        assertMoneyEquals("1.50", calc.calculateFinalPriceFor(Transaction.fromLine("2015-02-01 S LP"))); // already lowest
        assertMoneyEquals("6.90", calc.calculateFinalPriceFor(Transaction.fromLine("2015-02-01 L LP"))); // 1st large LP
        assertMoneyEquals("6.90", calc.calculateFinalPriceFor(Transaction.fromLine("2015-02-02 L LP"))); // 2nd large LP
        assertMoneyEquals("0.00", calc.calculateFinalPriceFor(Transaction.fromLine("2015-02-03 L LP"))); // 3rd -> free
    }

    @Test
    void freeThirdLargeResetsEachMonth() {
        PriceCalculator calc = new PriceCalculator();
        // February: the third large-LP is free
        calc.calculateFinalPriceFor(Transaction.fromLine("2015-02-01 L LP"));
        calc.calculateFinalPriceFor(Transaction.fromLine("2015-02-02 L LP"));
        assertMoneyEquals("0.00", calc.calculateFinalPriceFor(Transaction.fromLine("2015-02-03 L LP")));
        // March: counter and budget reset -> the third is free again
        calc.calculateFinalPriceFor(Transaction.fromLine("2015-03-01 L LP"));
        calc.calculateFinalPriceFor(Transaction.fromLine("2015-03-02 L LP"));
        assertMoneyEquals("0.00", calc.calculateFinalPriceFor(Transaction.fromLine("2015-03-03 L LP")));
    }

    private static void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }
}
