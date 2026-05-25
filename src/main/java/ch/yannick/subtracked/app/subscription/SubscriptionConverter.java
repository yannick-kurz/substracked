package ch.yannick.subtracked.app.subscription;

import ch.yannick.subtracked.app.subscription.dto.SubscriptionRequest;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionResponse;
import ch.yannick.subtracked.domain.category.Category;
import ch.yannick.subtracked.domain.category.CategoryRepository;
import ch.yannick.subtracked.domain.subscription.Subscription;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SubscriptionConverter {

    private final CategoryRepository categoryRepository;

    public SubscriptionConverter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Subscription toEntity(SubscriptionRequest request) {
        if (request == null) return null;

        Subscription subscription = new Subscription();
        updateEntity(subscription, request);
        return subscription;
    }

    public void updateEntity(Subscription subscription, SubscriptionRequest request) {
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Category not found: " + request.categoryId()));
            subscription.setCategory(category);
        }

        subscription.setName(request.name());
        subscription.setAmount(request.amount());
        subscription.setCurrency(request.currency());
        subscription.setBillingCycle(request.billingCycle());
        subscription.setNextRenewalDate(request.nextRenewalDate());
        subscription.setNotes(request.notes());
        subscription.setCatalogueEntryId(request.catalogueEntryId());
        subscription.setWebsite(request.website());
        subscription.setInTrial(request.inTrial());
        subscription.setTrialEndsAt(request.trialEndsAt());
    }

    public SubscriptionResponse convert(Subscription entity) {
        if (entity == null) return null;

        return new SubscriptionResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getName(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getBillingCycle(),
                entity.getNextRenewalDate(),
                entity.getPaidUntil(),
                entity.isActive(),
                entity.getCatalogueEntryId(),
                entity.getWebsite(),
                entity.getCreatedAt(),
                entity.isInTrial(),
                entity.getTrialEndsAt()
        );
    }
}