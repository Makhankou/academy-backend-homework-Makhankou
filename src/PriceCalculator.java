import java.math.BigDecimal;
import java.util.List;

/**
 * Computes a transaction's final price by running its base price through an
 * ordered chain of pricing rules, each of which may lower it. Some rules keep
 * per-month state (parcel counts, discount budgets), so one PriceCalculator
 * must be reused across all transactions in a run, a fresh one per transaction
 * would reset that state and the monthly rules would never trigger.
 */
public class PriceCalculator {

    // Order matters: discount-proposing rules first, the cap LAST.
    private final List<PricingRule> rules = List.of(
            new LowestSRule(),
            new ThirdLargeViaLpFreeRule(),
            new MonthlyCapRule()
    );

    public BigDecimal calculateFinalPriceFor(Transaction t) {
        BigDecimal price = t.basePrice();
        for (PricingRule rule : rules) {
            price = rule.apply(t, price);
        }
        return price;
    }
}
