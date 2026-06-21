import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One parsed line of input. Fields may be null when a token was missing or
 * unparseable — a Transaction is built even for malformed lines, and whether it
 * is usable is decided separately by isValid().
 *
 * @param date         the shipment date, or null if that token was missing/invalid
 * @param size         the parcel size, or null if missing/invalid
 * @param provider     the shipping provider, or null if missing/invalid
 * @param raw          the original line, kept so malformed lines can be echoed back
 * @param tooManyParts true when the line had more than the three expected tokens
 */
public record Transaction(LocalDate date, Size size, Provider provider, String raw, boolean tooManyParts) {

    // Build a Transaction from one raw line
    public static Transaction fromLine(String line) {
        String[] parts = line.trim().split("\\s+");
        LocalDate date = Parsing.parseDate(parts.length > 0 ? parts[0] : null);
        Size size = Parsing.parseSize(parts.length > 1 ? parts[1] : null);
        Provider provider = Parsing.parseProvider(parts.length > 2 ? parts[2] : null);
        boolean tooManyParts = parts.length > 3;
        return new Transaction(date, size, provider, line.trim(), tooManyParts);
    }

    // Check a Transaction validity
    public boolean isValid() {
        return !tooManyParts && date != null && size != null && provider != null;
    }

    // The list price for this provider and size, before any discount rules
    public BigDecimal basePrice() {
        return PriceList.priceFor(provider, size);
    }
}
