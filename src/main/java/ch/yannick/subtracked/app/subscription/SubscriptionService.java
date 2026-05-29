package ch.yannick.subtracked.app.subscription;

import ch.yannick.subtracked.app.exception.ResourceNotFoundException;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionRequest;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionResponse;
import ch.yannick.subtracked.domain.payment.Payment;
import ch.yannick.subtracked.domain.payment.PaymentRepository;
import ch.yannick.subtracked.domain.payment.PaymentResponse;
import ch.yannick.subtracked.domain.subscription.BillingCycle;
import ch.yannick.subtracked.domain.subscription.Subscription;
import ch.yannick.subtracked.domain.subscription.SubscriptionRepository;
import ch.yannick.subtracked.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionConverter converter;

    public SubscriptionService(SubscriptionRepository repository, PaymentRepository paymentRepository,
                               SubscriptionConverter converter) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.converter = converter;
    }

    @Transactional
    public SubscriptionResponse createSubscription(User user,
                                                   SubscriptionRequest request) {
        validateTrialFields(request);

        Subscription subscription = converter.toEntity(request);
        subscription.setUser(user);
        return converter.convert(repository.save(subscription));
    }

    @Transactional
    public SubscriptionResponse updateSubscription(User user, UUID id,
                                                   SubscriptionRequest request) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + id));

        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Subscription not found: " + id);
        }

        validateTrialFields(request);
        converter.updateEntity(subscription, request);
        return converter.convert(repository.save(subscription));
    }

    @Transactional
    public void deleteSubscription(User user, UUID id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + id));

        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Subscription not found: " + id);
        }

        repository.delete(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getAllSubscriptions(User user) {
        return repository.findByUserIdAndActiveTrue(user.getId())
                .stream()
                .map(converter::convert)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(User user, UUID id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + id));

        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Subscription not found: " + id);
        }

        return converter.convert(subscription);
    }

    @Transactional
    public void markPaid(User user, UUID id) {
        Subscription sub = repository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + id));

        LocalDate renewalDate = sub.getNextRenewalDate();

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setSubscriptionId(sub.getId());
        payment.setSubscriptionName(sub.getName());
        payment.setAmount(sub.getAmount());
        payment.setCurrency(sub.getCurrency());
        payment.setPaidOn(renewalDate);
        paymentRepository.save(payment);

        sub.setNextRenewalDate(nextDate(renewalDate, sub.getBillingCycle()));
        repository.save(sub);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getRecentPayments(User user, int limit) {
        return paymentRepository.findTop5ByUserIdOrderByPaidOnDesc(user.getId())
                .stream()
                .map(p -> new PaymentResponse(
                        p.getId(),
                        p.getSubscriptionId(),
                        p.getSubscriptionName(),
                        p.getAmount(),
                        p.getCurrency(),
                        p.getPaidOn()))
                .toList();
    }

    @Transactional
    public void markUnpaid(User user, UUID id) {
        Subscription sub = repository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + id));

        sub.setPaidUntil(null);
        repository.save(sub);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getUpcomingRenewals(User user, int days) {
        LocalDate from = LocalDate.now();
        LocalDate to   = from.plusDays(days);
        return repository.findByUserIdAndActiveTrue(user.getId())
                .stream()
                .filter(s -> !s.getNextRenewalDate().isBefore(from)
                        && !s.getNextRenewalDate().isAfter(to))
                .sorted(Comparator.comparing(Subscription::getNextRenewalDate))
                .map(converter::convert)
                .toList();
    }

    private LocalDate nextDate(LocalDate from, BillingCycle cycle) {
        return switch (cycle) {
            case WEEKLY    -> from.plusWeeks(1);
            case MONTHLY   -> from.plusMonths(1);
            case QUARTERLY -> from.plusMonths(3);
            case ANNUAL    -> from.plusYears(1);
        };
    }

    private void validateTrialFields(SubscriptionRequest request) {
        if (request.inTrial() && request.trialEndsAt() == null) {
            throw new IllegalArgumentException(
                    "trialEndsAt must be set when inTrial is true");
        }
        if (request.inTrial() && request.trialEndsAt().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "trialEndsAt must be in the future");
        }
    }
}