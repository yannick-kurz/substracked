package ch.yannick.subtracked.app.catalogue.dto;

import ch.yannick.subtracked.domain.subscription.BillingCycle;

import java.math.BigDecimal;

public record CatalogueResponse(
        Long id,
        String name,
        String provider,
        BigDecimal typicalAmount,
        String currency,
        BillingCycle billingCycle,
        Long categoryId,
        String logoUrl,
        String website,
        boolean active
) {
}
