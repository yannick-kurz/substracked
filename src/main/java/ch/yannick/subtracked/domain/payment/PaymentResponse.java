package ch.yannick.subtracked.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID subscriptionId,
        String subscriptionName,
        BigDecimal amount,
        String currency,
        LocalDate paidOn
) {}