package ch.yannick.subtracked.domain.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findByUserIdAndActiveTrue(UUID userId);

    List<Subscription> findByUserIdAndActiveTrueAndNextRenewalDateBetween(
            UUID userId,
            LocalDate from,
            LocalDate to
    );

    List<Subscription> findByNextRenewalDateAndActiveTrue(LocalDate nextRenewalDate);
}