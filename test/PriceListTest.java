import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceListTest {

    @Test
    void laPosteRates() {
        assertMoneyEquals("1.50", PriceList.priceFor(Provider.LP, Size.S));
        assertMoneyEquals("4.90", PriceList.priceFor(Provider.LP, Size.M));
        assertMoneyEquals("6.90", PriceList.priceFor(Provider.LP, Size.L));
    }

    @Test
    void mondialRelayRates() {
        assertMoneyEquals("2.00", PriceList.priceFor(Provider.MR, Size.S));
        assertMoneyEquals("3.00", PriceList.priceFor(Provider.MR, Size.M));
        assertMoneyEquals("4.00", PriceList.priceFor(Provider.MR, Size.L));
    }

    @Test
    void lowestSmallPriceIsLaPostes() {
        assertMoneyEquals("1.50", PriceList.LOWEST_S_PRICE);
    }

    private static void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }
}
