package ch.yannick.subtracked.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByUserIdOrderByPaidOnDesc(UUID userId);

    List<Payment> findTop5ByUserIdOrderByPaidOnDesc(UUID userId);

    void deleteByUserIdAndId(UUID userId, UUID paymentId);
}