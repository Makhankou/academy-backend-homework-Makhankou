# Shipping Price & Discount Calculator

A small Java program that reads a log of shipping transactions, applies a set of
pricing and discount rules, and prints each shipment's final price and the
discount that was applied. Lines that can't be understood are reported and
skipped rather than crashing the program.

## Input format

Each input line describes one shipment as three whitespace-separated fields:

```
<date> <size> <provider>
```

| Field      | Values                                   |
|------------|------------------------------------------|
| `date`     | `YYYY-MM-DD`                             |
| `size`     | `S` (small), `M` (medium), `L` (large)  |
| `provider` | `LP` (La Poste), `MR` (Mondial Relay)   |

Example line: `2015-02-01 S MR`

## Base prices

| Provider | S    | M    | L    |
|----------|------|------|------|
| LP       | 1.50 | 4.90 | 6.90 |
| MR       | 2.00 | 3.00 | 4.00 |

## Discount rules

The rules are applied in order, and the monthly cap is always applied last:

1. **Lowest small price** — every `S` shipment is charged the lowest `S` price
   across all providers (1.50), no matter which provider was chosen.
2. **Third large LP free** — the *third* `L` shipment via `LP` within a single
   calendar month ships free. Only that third one is free; the first, second,
   fourth, and later ones pay full price, and the count resets each month.
3. **Monthly discount cap** — total discounts may not exceed **10.00 per calendar
   month**. If a discount would exceed the remaining monthly budget, only the part
   that fits is applied (a partial discount); once the budget is spent, no further
   discounts are given that month.

## Output format

For each valid line the program prints:

```
<date> <size> <provider> <finalPrice> <discount>
```

The discount column shows `-` when no discount was applied. A line that fails
validation is echoed unchanged, followed by `Ignored`.

A line is considered invalid (and `Ignored`) when:

- the date is not a real calendar date (e.g. `2015-02-29` — 2015 is not a leap year),
- the size is not `S`, `M`, or `L`,
- the provider is not `LP` or `MR`,
- there are fewer than three fields, or
- there are more than three fields.

### Example

Input (`example/input.txt`):

```
2015-02-01 S MR
2015-02-02 S MR
2015-02-03 L LP
2015-02-05 S LP
2015-02-06 S MR
2015-02-06 L LP
2015-02-07 L MR
2015-02-08 M MR
2015-02-09 L LP
2015-02-10 L LP
2015-02-10 S MR
2015-02-10 S MR
2015-02-11 L LP
2015-02-12 M MR
2015-02-13 M LP
2015-02-15 S MR
2015-02-17 L LP
2015-02-17 S MR
2015-02-24 L LP
2015-02-29 CUSPS
2015-03-01 S MR
```

Output:

```
2015-02-01 S MR 1.50 0.50
2015-02-02 S MR 1.50 0.50
2015-02-03 L LP 6.90 -
2015-02-05 S LP 1.50 -
2015-02-06 S MR 1.50 0.50
2015-02-06 L LP 6.90 -
2015-02-07 L MR 4.00 -
2015-02-08 M MR 3.00 -
2015-02-09 L LP 0.00 6.90
2015-02-10 L LP 6.90 -
2015-02-10 S MR 1.50 0.50
2015-02-10 S MR 1.50 0.50
2015-02-11 L LP 6.90 -
2015-02-12 M MR 3.00 -
2015-02-13 M LP 4.90 -
2015-02-15 S MR 1.50 0.50
2015-02-17 L LP 6.90 -
2015-02-17 S MR 1.90 0.10
2015-02-24 L LP 6.90 -
2015-02-29 CUSPS Ignored
2015-03-01 S MR 1.50 0.50
```

A few lines worth pointing at:

- `2015-02-09 L LP 0.00 6.90` — the third large LP parcel of February ships free.
- `2015-02-17 S MR 1.90 0.10` — by this point February's discounts are nearly at
  the 10.00 cap, so this one is only partially discounted (0.10 instead of 0.50).
- `2015-02-29 CUSPS Ignored` — an invalid line, echoed and skipped.

## Requirements

- **JDK 16 or newer** (the code uses records and switch expressions).
  Developed and tested on recent JDKs (21+).

## Project structure

```
src/
  Main.java                      Entry point: reads the file, prints results.
  Transaction.java               One parsed line (record): factory, validity, base price.
  Parsing.java                   date / size / provider parsing helpers.
  PriceList.java                 The fixed price table and the lowest-S lookup.
  PricingRule.java               Interface: apply(transaction, currentPrice) -> newPrice.
  LowestSRule.java               Rule 1.
  ThirdLargeViaLpFreeRule.java   Rule 2.
  MonthlyCapRule.java            Rule 3 (must run last).
  PriceCalculator.java           Runs the rules in order.
  Size.java                      Enum: S, M, L.
  Provider.java                  Enum: MR, LP.
test/
  *Test.java                     JUnit 5 tests, one class per production class.
example/
  input.txt                      Sample input.
```

## Building and running

The program reads from the path given as the first argument, defaulting to
`example/input.txt`. If the file does not exist, it prints an error instead of
crashing.

### Terminal

```bash
# from the project root
javac -d out/production/academy-backend-homework-Makhankou src/*.java
java -cp out/production/academy-backend-homework-Makhankou Main example/input.txt
```

## Running the tests

The tests are written with **JUnit 5**.git

### Terminal

Using the JUnit Platform console launcher:

```bash
# one-time: download the launcher
mkdir -p lib
curl -L -o lib/junit-console.jar \
  https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.3/junit-platform-console-standalone-1.11.3.jar

# compile production code, then the tests
javac -d out/production/academy-backend-homework-Makhankou src/*.java
javac -cp "lib/junit-console.jar:out/production/academy-backend-homework-Makhankou" \
  -d out/test/academy-backend-homework-Makhankou test/*.java

# run all tests
java -jar lib/junit-console.jar execute \
  --class-path "out/production/academy-backend-homework-Makhankou:out/test/academy-backend-homework-Makhankou" \
  --scan-classpath
```

(On Windows, use `;` instead of `:` as the classpath separator.)

## Design notes

- **Money** is handled with `BigDecimal` throughout, created from string literals
  (e.g. `new BigDecimal("1.50")`) and compared with `compareTo`, to avoid
  floating-point rounding errors.
- **Rules** share a single interface, `PricingRule`. `PriceCalculator` applies
  them in sequence: each rule receives the running price and returns a possibly
  adjusted price. The discount-proposing rules run first; the monthly cap runs
  last, because it reads `basePrice − currentPrice` as the discount proposed so
  far and clamps the monthly total.
- **Cross-transaction state** (per-month large-LP counts, per-month discount used)
  lives inside the individual rule objects, keyed by calendar month via
  `YearMonth`. `PriceCalculator` is created once so this state accumulates across
  the whole run.
- **The price table** is isolated in `PriceList` as a static lookup, since the
  rates are fixed; `Transaction.basePrice()` delegates to it.
- **Adding a rule** means writing a class that implements `PricingRule` and adding
  it to the list in `PriceCalculator` (keeping the monthly cap last).
