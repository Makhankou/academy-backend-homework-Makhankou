import java.math.BigDecimal;

public interface PricingRule {
    BigDecimal apply(Transaction t, BigDecimal currentPrice);
}
