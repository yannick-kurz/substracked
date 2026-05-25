package ch.yannick.subtracked.domain.catalogue;

import ch.yannick.subtracked.domain.category.Category;
import ch.yannick.subtracked.domain.subscription.BillingCycle;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "catalogue_entries")
public class CatalogueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String provider;

    @Column(precision = 10, scale = 2)
    private BigDecimal typicalAmount;

    @Column(length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String logoUrl;

    private String website;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    protected void onCreate() {
        this.active = true;
    }

    public CatalogueEntry() {}

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public BigDecimal getTypicalAmount() { return typicalAmount; }
    public void setTypicalAmount(BigDecimal typicalAmount) { this.typicalAmount = typicalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BillingCycle getBillingCycle() { return billingCycle; }
    public void setBillingCycle(BillingCycle billingCycle) { this.billingCycle = billingCycle; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}