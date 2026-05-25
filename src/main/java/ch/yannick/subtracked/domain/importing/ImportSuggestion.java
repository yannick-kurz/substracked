package ch.yannick.subtracked.domain.importing;

import ch.yannick.subtracked.domain.user.User;
import ch.yannick.subtracked.domain.subscription.BillingCycle;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "import_suggestions")
public class ImportSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String merchantName;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    private BillingCycle detectedCycle;

    private LocalDate estimatedNextRenewal;

    private Integer occurrenceCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportSuggestionStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ImportSuggestionStatus.PENDING;
        }
    }

    public ImportSuggestion() {}

    public UUID getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BillingCycle getDetectedCycle() { return detectedCycle; }
    public void setDetectedCycle(BillingCycle detectedCycle) { this.detectedCycle = detectedCycle; }

    public LocalDate getEstimatedNextRenewal() { return estimatedNextRenewal; }
    public void setEstimatedNextRenewal(LocalDate estimatedNextRenewal) { this.estimatedNextRenewal = estimatedNextRenewal; }

    public Integer getOccurrenceCount() { return occurrenceCount; }
    public void setOccurrenceCount(Integer occurrenceCount) { this.occurrenceCount = occurrenceCount; }

    public ImportSuggestionStatus getStatus() { return status; }
    public void setStatus(ImportSuggestionStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}