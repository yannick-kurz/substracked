package ch.yannick.subtracked.app.importing;

import ch.yannick.subtracked.app.importing.dto.CsvTransaction;
import ch.yannick.subtracked.app.importing.dto.ImportSuggestionRequest;
import ch.yannick.subtracked.domain.subscription.BillingCycle;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RecurrenceDetector {


    private List<CsvTransaction> deduplicate(List<CsvTransaction> transactions) {
        return transactions.stream()
                .distinct()
                .toList();
    }

    private static final Set<String> NOISE_WORDS = Set.of(
            "SWITZERLAND", "SCHWEIZ", "SUISSE", "GMBH", "AG", "SA",
            "INTL", "INT", "INTERNATIONAL", "COM", "CH", "LTD",
            "BV", "NV", "INC", "CORP", "AB", "SRL"
    );

    private String normalizeMerchant(String description) {
        if (description == null || description.isBlank()) return "";

        String cleaned = description
                .toUpperCase()
                .replaceAll("[^A-Z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        String[] words = cleaned.split(" ");

        return Arrays.stream(words)
                .filter(w -> w.length() > 2)
                .filter(w -> !w.matches("[0-9]+"))
                .filter(w -> !NOISE_WORDS.contains(w))
                .findFirst()
                .orElse(words[0]);
    }

    private Map<String, List<CsvTransaction>> groupByMerchant(
            List<CsvTransaction> transactions) {
        if (transactions == null || transactions.isEmpty()) return Map.of();

        return transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> normalizeMerchant(transaction.description())
                ));
    }

    private List<Long> calculateIntervals(List<CsvTransaction> sorted) {
        List<Long> intervals = new ArrayList<>();

        for (int i = 1; i < sorted.size(); i++) {
            long days = ChronoUnit.DAYS.between(
                    sorted.get(i - 1).transactionDate(),
                    sorted.get(i).transactionDate()
            );
            intervals.add(days);
        }

        return intervals;
    }

    private Optional<BillingCycle> detectCycle(List<Long> intervals) {
        if (intervals == null || intervals.isEmpty()) return Optional.empty();

        double avg = intervals.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        if (avg >= 25 && avg <= 38)  return Optional.of(BillingCycle.MONTHLY);
        if (avg >= 80 && avg <= 100) return Optional.of(BillingCycle.QUARTERLY);
        if (avg >= 350 && avg <= 380) return Optional.of(BillingCycle.ANNUAL);

        return Optional.empty();
    }

    private boolean isRecentEnough(List<CsvTransaction> sorted, BillingCycle cycle) {
        LocalDate lastDate = sorted.getLast().transactionDate();
        long daysSinceLast = ChronoUnit.DAYS.between(lastDate, LocalDate.now());

        return switch (cycle) {
            case MONTHLY   -> daysSinceLast <= 45;
            case QUARTERLY -> daysSinceLast <= 105;
            case ANNUAL    -> daysSinceLast <= 400;
            default        -> false;
        };
    }

    private ImportSuggestionRequest buildSuggestion(
            String merchantName, List<CsvTransaction> transactions, BillingCycle billingCycle) {

        LocalDate lastDate = transactions.getLast().transactionDate();

        LocalDate renewalDate = switch (billingCycle) {
            case WEEKLY    -> lastDate.plusWeeks(1);
            case MONTHLY   -> lastDate.plusMonths(1);
            case QUARTERLY -> lastDate.plusMonths(3);
            case ANNUAL    -> lastDate.plusMonths(12);
        };

        return new ImportSuggestionRequest(
                merchantName,
                transactions.getLast().amount(),
                transactions.getLast().currency(),
                billingCycle,
                renewalDate,
                transactions.size()
        );
    }

    public List<ImportSuggestionRequest> detect(List<CsvTransaction> transactions) {
        var cleaned = deduplicate(transactions);
        var grouped = groupByMerchant(cleaned);

        List<ImportSuggestionRequest> suggestions = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            String merchantName = entry.getKey();
            List<CsvTransaction> group = entry.getValue();

            if (group.size() < 2) continue;

            List<CsvTransaction> sorted = group.stream()
                    .sorted(Comparator.comparing(CsvTransaction::transactionDate))
                    .toList();

            List<Long> intervals = calculateIntervals(sorted);

            Optional<BillingCycle> cycle = detectCycle(intervals);
            if (cycle.isEmpty()) continue;

            if (!isRecentEnough(sorted, cycle.get())) continue;

            suggestions.add(buildSuggestion(merchantName, sorted, cycle.get()));
        }

        return suggestions;
    }


}