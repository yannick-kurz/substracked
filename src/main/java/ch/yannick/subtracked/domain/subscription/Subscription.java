package ch.yannick.subtracked.domain.subscription;

import ch.yannick.subtracked.domain.category.Category;
import ch.yannick.subtracked.domain.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingCycle billingCycle;

    @Column(nullable = false)
    private LocalDate nextRenewalDate;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "website")
    private String website;

    private String notes;

    private boolean inTrial;

    private LocalDate trialEndsAt;

    private Integer catalogueEntryId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    @Column(name = "paid_until")
    private LocalDate paidUntil;

    public boolean isPaidUpToDate() {
        return paidUntil != null && !paidUntil.isBefore(LocalDate.now());
    }

    public Subscription() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BillingCycle getBillingCycle() { return billingCycle; }
    public void setBillingCycle(BillingCycle billingCycle) { this.billingCycle = billingCycle; }

    public LocalDate getNextRenewalDate() { return nextRenewalDate; }
    public void setNextRenewalDate(LocalDate nextRenewalDate) { this.nextRenewalDate = nextRenewalDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getTrialEndsAt() { return trialEndsAt; }
    public void setTrialEndsAt(LocalDate trialEndsAt) { this.trialEndsAt = trialEndsAt; }

    public boolean isInTrial() { return inTrial; }
    public void setInTrial(boolean inTrial) { this.inTrial = inTrial; }

    public Integer getCatalogueEntryId() { return catalogueEntryId; }
    public void setCatalogueEntryId(Integer catalogueEntryId) { this.catalogueEntryId = catalogueEntryId; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDate getPaidUntil() { return paidUntil; }
    public void setPaidUntil(LocalDate paidUntil) { this.paidUntil = paidUntil; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}