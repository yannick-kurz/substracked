package ch.yannick.subtracked.app.importing.dto;

import ch.yannick.subtracked.domain.subscription.BillingCycle;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ImportSuggestionRequest(
        String merchantName,
        BigDecimal amount,
        String currency,
        BillingCycle detectedCycle,
        LocalDate estimatedNextRenewal,
        int occurrenceCount
) {}