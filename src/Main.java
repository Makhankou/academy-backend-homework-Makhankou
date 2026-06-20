import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {

    public static void main(String[] args) throws IOException {
        String path = args.length > 0 ? args[0] : "example/input.txt";

        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(path));
        } catch (NoSuchFileException e) {
            System.err.println("Error: input file not found: " + path);
            return;
        }

        List<Transaction> transactions = new ArrayList<>();

        for (String line : lines) {
            if (line.isBlank())
                continue;
            transactions.add(Transaction.fromLine(line));
        }

        // Price valid lines, echo the rest
        PriceCalculator calc = new PriceCalculator(); // once: its state accumulates

        for (Transaction t : transactions) {
            if (t.isValid()) {
                BigDecimal originalPrice = t.basePrice();
                BigDecimal finalPrice = calc.calculateFinalPriceFor(t);
                BigDecimal discount = originalPrice.subtract(finalPrice);

                String discountText = discount.signum() == 0
                        ? "-"
                        : String.format(Locale.US, "%.2f", discount);

                System.out.printf(Locale.US, "%s %s %s %.2f %s%n",
                        t.date(), t.size(), t.provider(), finalPrice, discountText);
            } else {
                System.out.println(t.raw() + " Ignored");
            }
        }
    }
}
