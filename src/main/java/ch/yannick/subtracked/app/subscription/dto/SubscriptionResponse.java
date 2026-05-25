package ch.yannick.subtracked.app.subscription.dto;

import ch.yannick.subtracked.domain.subscription.BillingCycle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubscriptionResponse(
        UUID subscriptionId,
        UUID userId,
        Long categoryId,
        String name,
        BigDecimal amount,
        String currency,
        BillingCycle billingCycle,
        LocalDate nextRenewalDate,
        LocalDate paidUntil,
        boolean active,
        Integer catalogueEntry,
        String website,
        LocalDateTime createdAt,
        boolean inTrial,
        LocalDate trialEndsAt
) {}