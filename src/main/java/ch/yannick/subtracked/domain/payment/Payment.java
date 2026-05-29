package ch.yannick.subtracked.domain.payment;

import ch.yannick.subtracked.domain.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "subscription_id")
    private UUID subscriptionId;

    @Column(name = "subscription_name", nullable = false)
    private String subscriptionName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "paid_on", nullable = false)
    private LocalDate paidOn;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public UUID getId()                        { return id; }
    public User getUser()                      { return user; }
    public void setUser(User user)             { this.user = user; }
    public UUID getSubscriptionId()            { return subscriptionId; }
    public void setSubscriptionId(UUID id)     { this.subscriptionId = id; }
    public String getSubscriptionName()        { return subscriptionName; }
    public void setSubscriptionName(String n)  { this.subscriptionName = n; }
    public BigDecimal getAmount()              { return amount; }
    public void setAmount(BigDecimal amount)   { this.amount = amount; }
    public String getCurrency()                { return currency; }
    public void setCurrency(String currency)   { this.currency = currency; }
    public LocalDate getPaidOn()               { return paidOn; }
    public void setPaidOn(LocalDate paidOn)    { this.paidOn = paidOn; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
}
