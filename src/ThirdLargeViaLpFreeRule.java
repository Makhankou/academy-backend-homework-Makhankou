import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/** Rule 2: the 3rd L shipment via LP each calendar month is free. */
public class ThirdLargeViaLpFreeRule implements PricingRule {
    private final Map<YearMonth, Integer> lpLargeCounts = new HashMap<>();

    @Override
    public BigDecimal apply(Transaction t, BigDecimal currentPrice) {
        if (t.provider() == Provider.LP && t.size() == Size.L) {
            YearMonth month = YearMonth.from(t.date());
            int countThisMonth = lpLargeCounts.merge(month, 1, Integer::sum);
            if (countThisMonth == 3) {
                return BigDecimal.ZERO;   // exactly the third — and only it
            }
        }
        return currentPrice;
    }
}
