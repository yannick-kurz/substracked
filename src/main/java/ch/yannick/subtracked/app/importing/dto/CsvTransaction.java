package ch.yannick.subtracked.app.importing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CsvTransaction(
        LocalDate transactionDate,
        String accountOrCardNumber,
        String description,
        String incomeOrExpense,
        BigDecimal amount,
        String currency
) {
    public boolean isExpense() {
        return "Expense".equalsIgnoreCase(incomeOrExpense);
    }
}