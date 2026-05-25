package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.app.subscription.SubscriptionService;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionResponse;
import ch.yannick.subtracked.domain.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Controller
public class DashboardController {

    private final SubscriptionService subscriptionService;

    public DashboardController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        List<SubscriptionResponse> subs = subscriptionService.getAllSubscriptions(user);
        List<SubscriptionResponse> upcoming = subscriptionService.getUpcomingRenewals(user, 7);

        BigDecimal monthly = subs.stream()
                .map(SubscriptionResponse::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        SubscriptionResponse next = subs.stream()
                .filter(SubscriptionResponse::active)
                .filter(s -> s.nextRenewalDate() != null)
                .min(Comparator.comparing(SubscriptionResponse::nextRenewalDate))
                .orElse(null);

        model.addAttribute("currentUser", user);
        model.addAttribute("subscriptions", subs);
        model.addAttribute("upcoming", upcoming);
        model.addAttribute("monthlyCost", monthly);
        model.addAttribute("annualCost", monthly.multiply(BigDecimal.valueOf(12))
                .setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("nextRenewal", next);
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}