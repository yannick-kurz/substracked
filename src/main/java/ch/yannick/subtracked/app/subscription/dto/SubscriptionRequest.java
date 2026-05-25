package ch.yannick.subtracked.app.subscription.dto;

import ch.yannick.subtracked.domain.subscription.BillingCycle;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SubscriptionRequest(
        Long categoryId,
        @NotBlank @Size(max = 100) String name,
        @NotNull @Positive BigDecimal amount,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotNull BillingCycle billingCycle,
        @NotNull LocalDate nextRenewalDate,
        String notes,
        String website,
        Integer catalogueEntryId,
        boolean inTrial,
        LocalDate trialEndsAt
) {}