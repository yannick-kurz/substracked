package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.app.subscription.SubscriptionService;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionResponse;
import ch.yannick.subtracked.domain.payment.Payment;
import ch.yannick.subtracked.domain.payment.PaymentRepository;
import ch.yannick.subtracked.domain.payment.PaymentResponse;
import ch.yannick.subtracked.domain.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
public class DashboardController {

    private final SubscriptionService subscriptionService;
    private final PaymentRepository   paymentRepository;

    public DashboardController(SubscriptionService subscriptionService,
                               PaymentRepository paymentRepository) {
        this.subscriptionService = subscriptionService;
        this.paymentRepository   = paymentRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {

        // ── Subscriptions ────────────────────────────────────────
        List<SubscriptionResponse> subs =
                subscriptionService.getAllSubscriptions(user);

        BigDecimal monthly = subs.stream()
                .map(SubscriptionResponse::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal annual = monthly
                .multiply(BigDecimal.valueOf(12))
                .setScale(2, RoundingMode.HALF_UP);

        SubscriptionResponse nextRenewal = subs.stream()
                .filter(SubscriptionResponse::active)
                .filter(s -> s.nextRenewalDate() != null)
                .min(Comparator.comparing(SubscriptionResponse::nextRenewalDate))
                .orElse(null);

        List<SubscriptionResponse> upcoming =
                subscriptionService.getUpcomingRenewals(user, 7);

        // ── Payments ─────────────────────────────────────────────
        LocalDate now = LocalDate.now();

        List<Payment> allPayments =
                paymentRepository.findByUserIdOrderByPaidOnDesc(user.getId());

        BigDecimal thisMonthPaid = allPayments.stream()
                .filter(p -> p.getPaidOn().getMonth() == now.getMonth()
                        && p.getPaidOn().getYear()  == now.getYear())
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        List<PaymentResponse> recentPayments =
                paymentRepository.findTop5ByUserIdOrderByPaidOnDesc(user.getId())
                        .stream()
                        .map(p -> new PaymentResponse(
                                p.getId(),
                                p.getSubscriptionId(),
                                p.getSubscriptionName(),
                                p.getAmount(),
                                p.getCurrency(),
                                p.getPaidOn()))
                        .toList();

        // ── Model ────────────────────────────────────────────────
        model.addAttribute("currentUser",    user);
        model.addAttribute("subscriptions",  subs);
        model.addAttribute("upcoming",       upcoming);
        model.addAttribute("monthlyCost",    monthly);
        model.addAttribute("annualCost",     annual);
        model.addAttribute("nextRenewal",    nextRenewal);
        model.addAttribute("thisMonthPaid",  thisMonthPaid);
        model.addAttribute("recentPayments", recentPayments);
        model.addAttribute("activePage",     "dashboard");

        return "dashboard";
    }
}