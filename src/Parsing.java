import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
