import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Entry point. Reads shipping transactions from a file (path given as the first
 * argument, or "example/input.txt" by default), prices each valid line through
 * the discount rules and prints it, and echoes malformed lines with "Ignored".
 * If the file is missing, it stops with an error message.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        // Input file: the first command-line argument, or a default path.
        String path = args.length > 0 ? args[0] : "example/input.txt";

        // Read the whole file up front; stop cleanly if it isn't there.
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(path));
        } catch (NoSuchFileException e) {
            System.err.println("Error: input file not found: " + path);
            return;
        }

        // Parse every non-blank line into a Transaction (valid or not).
        List<Transaction> transactions = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank())
                continue;
            transactions.add(Transaction.fromLine(line));
        }

        // Price the valid transactions and print each; echo the rest as "Ignored".
        // Created once, so the rules' monthly state accumulates across all lines.
        PriceCalculator calc = new PriceCalculator(); // once: its state accumulates

        for (Transaction t : transactions) {
            if (t.isValid()) {
                BigDecimal originalPrice = t.basePrice();
                BigDecimal finalPrice = calc.calculateFinalPriceFor(t);
                BigDecimal discount = originalPrice.subtract(finalPrice);

                // Show the discount amount, or "-" when there is none.
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
