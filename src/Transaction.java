import java.math.BigDecimal;
import java.time.LocalDate;

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

    // Base price straight from the table
    public BigDecimal basePrice() {
        return PriceList.priceFor(provider, size);
    }
}
