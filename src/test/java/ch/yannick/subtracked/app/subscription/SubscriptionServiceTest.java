package ch.yannick.subtracked.app.subscription;

import ch.yannick.subtracked.app.exception.ResourceNotFoundException;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionRequest;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionResponse;
import ch.yannick.subtracked.domain.subscription.BillingCycle;
import ch.yannick.subtracked.domain.subscription.Subscription;
import ch.yannick.subtracked.domain.subscription.SubscriptionRepository;
import ch.yannick.subtracked.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository repository;

    @Mock
    private SubscriptionConverter converter;

    @InjectMocks
    private SubscriptionService service;

    private User user;
    private Subscription subscription;
    private SubscriptionResponse response;
    private SubscriptionRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");

        subscription = new Subscription();
        subscription.setId(UUID.randomUUID());
        subscription.setUser(user);
        subscription.setName("Netflix");
        subscription.setAmount(new BigDecimal("13.99"));
        subscription.setCurrency("CHF");
        subscription.setBillingCycle(BillingCycle.MONTHLY);
        subscription.setNextRenewalDate(LocalDate.now().plusMonths(1));
        subscription.setActive(true);

        response = new SubscriptionResponse(
                subscription.getId(), user.getId(), null,
                "Netflix", new BigDecimal("13.99"), "CHF",
                BillingCycle.MONTHLY, LocalDate.now().plusMonths(1),
                null, true, null, null, null, false, null);

        request = new SubscriptionRequest(
                null, "Netflix", new BigDecimal("13.99"), "CHF",
                BillingCycle.MONTHLY, LocalDate.now().plusMonths(1),
                null, null, null, false, null);
    }

    @Test
    void getAllSubscriptions_returnsListForUser() {
        when(repository.findByUserIdAndActiveTrue(user.getId()))
                .thenReturn(List.of(subscription));
        when(converter.convert(subscription)).thenReturn(response);

        List<SubscriptionResponse> result = service.getAllSubscriptions(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Netflix");
    }

    @Test
    void createSubscription_savesAndReturnsResponse() {
        when(converter.toEntity(request)).thenReturn(subscription);
        when(repository.save(any())).thenReturn(subscription);
        when(converter.convert(subscription)).thenReturn(response);

        SubscriptionResponse result = service.createSubscription(user, request);

        assertThat(result.name()).isEqualTo("Netflix");
        verify(repository).save(any());
    }

    @Test
    void deleteSubscription_throwsWhenNotOwner() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        when(repository.findById(subscription.getId()))
                .thenReturn(Optional.of(subscription));

        assertThatThrownBy(() ->
                service.deleteSubscription(otherUser, subscription.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void markPaid_setsPaidUntilAndAdvancesRenewalDate() {
        LocalDate originalRenewal = LocalDate.now().plusMonths(1);
        subscription.setNextRenewalDate(originalRenewal);

        when(repository.findById(subscription.getId()))
                .thenReturn(Optional.of(subscription));
        when(repository.save(any())).thenReturn(subscription);

        service.markPaid(user, subscription.getId());

        assertThat(subscription.getPaidUntil()).isEqualTo(originalRenewal);
        assertThat(subscription.getNextRenewalDate())
                .isEqualTo(originalRenewal.plusMonths(1));
    }
}