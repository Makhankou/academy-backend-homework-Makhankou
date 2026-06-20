import java.math.BigDecimal;
import java.util.List;

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
