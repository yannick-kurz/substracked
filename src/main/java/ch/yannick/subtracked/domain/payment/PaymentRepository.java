package ch.yannick.subtracked.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByUserIdOrderByPaidOnDesc(UUID userId);

    List<Payment> findTop5ByUserIdOrderByPaidOnDesc(UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Payment p WHERE p.id = :id AND p.user.id = :userId")
    void deleteByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Payment p WHERE p.user.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}