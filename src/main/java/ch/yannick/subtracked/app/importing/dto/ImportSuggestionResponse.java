package ch.yannick.subtracked.app.importing.dto;

import ch.yannick.subtracked.domain.importing.ImportSuggestionStatus;
import ch.yannick.subtracked.domain.subscription.BillingCycle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ImportSuggestionResponse(
        UUID id,
        String merchantName,
        BigDecimal amount,
        String currency,
        BillingCycle detectedCycle,
        LocalDate estimatedNextRenewal,
        int occurrenceCount,
        ImportSuggestionStatus status
) {}
