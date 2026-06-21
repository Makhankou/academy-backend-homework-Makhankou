import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Parsers for the fields of a transaction line. Each method turns
 * one token into its typed value and returns null instead of throwing when
 * the token is missing or unrecognized, so the caller can still build a
 * Transaction and let isValid() decide whether to keep it.
 */

public class Parsing {
    static LocalDate parseDate(String s) {
        if (s == null) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    static Size parseSize(String s) {
        if (s == null) return null;
        try {
            return Size.valueOf(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    static Provider parseProvider(String s) {
        if (s == null) return null;
        try {
            return Provider.valueOf(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
