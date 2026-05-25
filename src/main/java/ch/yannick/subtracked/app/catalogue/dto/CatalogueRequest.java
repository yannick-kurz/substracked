//package ch.yannick.subtracked.app.catalogue.dto;
//
//import ch.yannick.subtracked.domain.subscription.BillingCycle;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
//import jakarta.validation.constraints.Size;
//
//import java.math.BigDecimal;
//
//public record CatalogueRequest(
//        @NotBlank @Size(max = 100) String name,
//        @NotBlank String provider,
//        @NotNull @Positive BigDecimal typicalAmount,
//        @NotBlank @Size(min = 3, max = 3) String currency,
//        @NotNull BillingCycle billingCycle,
//        Long categoryId,
//        String logoUrl,
//        @NotBlank String website,
//        boolean active
//) {
//}
