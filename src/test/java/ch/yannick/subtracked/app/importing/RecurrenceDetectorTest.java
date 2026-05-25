package ch.yannick.subtracked.app.importing;

import ch.yannick.subtracked.app.importing.dto.CsvTransaction;
import ch.yannick.subtracked.app.importing.dto.ImportSuggestionRequest;
import ch.yannick.subtracked.domain.subscription.BillingCycle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecurrenceDetectorTest {

    private final RecurrenceDetector detector = new RecurrenceDetector();

    @Test
    void detect_identifiesMonthlyRecurrence() {
        List<CsvTransaction> transactions = List.of(
                new CsvTransaction(LocalDate.now().minusMonths(3),"12345",
                        "NETFLIX", "Expense", new BigDecimal("13.99"), "CHF"),
                new CsvTransaction(LocalDate.now().minusMonths(2),"12345",
                        "NETFLIX", "Expense", new BigDecimal("13.99"), "CHF"),
                new CsvTransaction(LocalDate.now().minusMonths(1),"12345",
                        "NETFLIX", "Expense", new BigDecimal("13.99"), "CHF")
        );

        List<ImportSuggestionRequest> suggestions = detector.detect(transactions);

        assertThat(suggestions).isNotEmpty();
        assertThat(suggestions.get(0).merchantName()).contains("NETFLIX");
        assertThat(suggestions.get(0).detectedCycle()).isEqualTo(BillingCycle.MONTHLY);
    }

    @Test
    void detect_ignoresSingleTransactions() {
        List<CsvTransaction> transactions = List.of(
                new CsvTransaction(LocalDate.now().minusMonths(1), "12345",
                        "AMAZON", "Expense", new BigDecimal("29.99"), "CHF")
        );

        List<ImportSuggestionRequest> suggestions = detector.detect(transactions);

        assertThat(suggestions).isEmpty();
    }

    @Test
    void detect_identifiesAnnualRecurrence() {
        List<CsvTransaction> transactions = List.of(
                new CsvTransaction(LocalDate.now().minusYears(2), "12345",
                        "ADOBE", "Expense", new BigDecimal("54.99"), "CHF"),
                new CsvTransaction(LocalDate.now().minusYears(1),"12345",
                        "ADOBE", "Expense", new BigDecimal("54.99"), "CHF")
        );

        List<ImportSuggestionRequest> suggestions = detector.detect(transactions);

        assertThat(suggestions).isNotEmpty();
        assertThat(suggestions.get(0).detectedCycle()).isEqualTo(BillingCycle.ANNUAL);
    }

    @Test
    void detect_ignoresNonRecurringPattern() {
        List<CsvTransaction> transactions = List.of(
                new CsvTransaction(LocalDate.now().minusDays(5), "12345",
                        "MIGROS", "Expense", new BigDecimal("42.30"), "CHF"),
                new CsvTransaction(LocalDate.now().minusDays(12), "12345",
                        "MIGROS", "Expense", new BigDecimal("38.50"), "CHF"),
                new CsvTransaction(LocalDate.now().minusDays(20), "12345",
                        "MIGROS", "Expense", new BigDecimal("55.10"), "CHF")
        );

        List<ImportSuggestionRequest> suggestions = detector.detect(transactions);

        assertThat(suggestions).isEmpty();
    }
}