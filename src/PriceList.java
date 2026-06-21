import java.math.BigDecimal;

/**
 * The fixed price table. Not instantiable, but a static lookup.
 */
public class PriceList {

    private PriceList() {
    }   // no instances

    public static BigDecimal priceFor(Provider provider, Size size) {
        return switch (provider) {
            case LP -> switch (size) {
                case S -> new BigDecimal("1.50");
                case M -> new BigDecimal("4.90");
                case L -> new BigDecimal("6.90");
            };
            case MR -> switch (size) {
                case S -> new BigDecimal("2.00");
                case M -> new BigDecimal("3.00");
                case L -> new BigDecimal("4.00");
            };
        };
    }

    // Lowest S price across providers, computed once.
    public static final BigDecimal LOWEST_S_PRICE = lowestPriceForSize(Size.S);

    // Lowest price across providers for a selected size
    private static BigDecimal lowestPriceForSize(Size size) {
        BigDecimal lowest = null;
        for (Provider provider : Provider.values()) {
            BigDecimal candidate = priceFor(provider, size);
            if (lowest == null || candidate.compareTo(lowest) < 0) {
                lowest = candidate;
            }
        }
        return lowest;
    }
}
