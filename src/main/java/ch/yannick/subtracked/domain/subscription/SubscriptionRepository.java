package ch.yannick.subtracked.domain.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Modifying
    @Query("DELETE FROM Subscription s WHERE s.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}