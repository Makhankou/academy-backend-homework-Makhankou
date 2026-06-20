import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/**
 * Rule 3: discounts total at most 10 per calendar month; partial if needed.
 * MUST run LAST: it reads (basePrice - currentPrice) as the discount the
 * earlier rules proposed, then caps the running monthly total.
 */
public class MonthlyCapRule implements PricingRule {
    private static final BigDecimal MONTHLY_DISCOUNT_CAP = new BigDecimal("10.00");
    private final Map<YearMonth, BigDecimal> discountUsed = new HashMap<>();

    @Override
    public BigDecimal apply(Transaction t, BigDecimal currentPrice) {
        BigDecimal wantedDiscount = t.basePrice().subtract(currentPrice);
        if (wantedDiscount.signum() == 0) {
            return currentPrice;   // nothing was proposed — nothing to cap
        }
        YearMonth month = YearMonth.from(t.date());
        BigDecimal used = discountUsed.getOrDefault(month, BigDecimal.ZERO);
        BigDecimal remaining = MONTHLY_DISCOUNT_CAP.subtract(used);

        BigDecimal granted = wantedDiscount.min(remaining);   // only what fits
        discountUsed.put(month, used.add(granted));
        return t.basePrice().subtract(granted);
    }
}
