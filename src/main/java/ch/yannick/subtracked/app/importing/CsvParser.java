package ch.yannick.subtracked.app.importing;

import ch.yannick.subtracked.app.importing.dto.CsvTransaction;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvParser {

    private static final DateTimeFormatter UBS_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final DateTimeFormatter ISO_DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    public List<CsvTransaction> parse(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty or missing");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("File must be a CSV");
        }

        List<CsvTransaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(),
                        StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.isBlank()) continue;

                CsvTransaction transaction = parseLine(line);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to parse CSV file: " + e.getMessage());
        }

        if (transactions.isEmpty()) {
            throw new IllegalArgumentException(
                    "No valid transactions found in CSV");
        }

        return transactions;
    }

    private CsvTransaction parseLine(String line) {
        String[] columns = line.split(",", -1);

        if (columns.length < 6) return null;

        try {
            LocalDate date = parseDate(clean(columns[0]));
            String account = clean(columns[1]);
            String description = clean(columns[2]);
            String type = clean(columns[3]);
            BigDecimal amount = parseAmount(clean(columns[4]));
            String currency = clean(columns[5]);

            if (description.isBlank() || amount == null) return null;

            return new CsvTransaction(
                    date,
                    account,
                    description,
                    type,
                    amount,
                    currency
            );

        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return LocalDate.parse(raw, UBS_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(raw, ISO_DATE_FORMAT);
        }
    }

    private BigDecimal parseAmount(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            String normalized = raw
                    .replace("'", "");
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String clean(String value) {
        if (value == null) return "";
        return value.trim()
                .replace("\"", "")
                .replace("\uFEFF", "");
    }
}