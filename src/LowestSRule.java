import java.math.BigDecimal;

/** Rule 1: every S matches the lowest S price across providers. Stateless. */
public class LowestSRule implements PricingRule {
    @Override
    public BigDecimal apply(Transaction t, BigDecimal currentPrice) {
        if (t.size() == Size.S) {
            return PriceList.LOWEST_S_PRICE;
        }
        return currentPrice;
    }
}
