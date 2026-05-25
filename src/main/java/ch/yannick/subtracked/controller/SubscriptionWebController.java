package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.app.subscription.SubscriptionService;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionRequest;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionResponse;
import ch.yannick.subtracked.domain.category.CategoryRepository;
import ch.yannick.subtracked.domain.subscription.BillingCycle;
import ch.yannick.subtracked.domain.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionWebController {

    private final SubscriptionService subscriptionService;
    private final CategoryRepository categoryRepository;

    public SubscriptionWebController(SubscriptionService subscriptionService,
                                     CategoryRepository categoryRepository) {
        this.subscriptionService = subscriptionService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("subscriptions", subscriptionService.getAllSubscriptions(user));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("billingCycles", BillingCycle.values());
        model.addAttribute("activePage", "subscriptions");
        return "subscriptions";
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("billingCycles", BillingCycle.values());
        model.addAttribute("activePage", "subscriptions");
        return "subscription-form";
    }

    @PostMapping("/new")
    public String create(@AuthenticationPrincipal User user,
                         @RequestParam String name,
                         @RequestParam BigDecimal amount,
                         @RequestParam String currency,
                         @RequestParam BillingCycle billingCycle,
                         @RequestParam LocalDate nextRenewalDate,
                         @RequestParam(required = false) Long categoryId,
                         @RequestParam(required = false) String notes,
                         @RequestParam(required = false) String website,
                         Model model) {
        try {
            subscriptionService.createSubscription(user, new SubscriptionRequest(
                    categoryId, name, amount, currency,
                    billingCycle, nextRenewalDate, notes, website, null, false, null));
            return "redirect:/subscriptions";
        } catch (Exception e) {
            model.addAttribute("currentUser", user);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("billingCycles", BillingCycle.values());
            return "subscription-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@AuthenticationPrincipal User user,
                           @PathVariable UUID id,
                           Model model) {
        try {
            SubscriptionResponse sub = subscriptionService.getSubscriptionById(user, id);
            model.addAttribute("currentUser", user);
            model.addAttribute("subscription", sub);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("billingCycles", BillingCycle.values());
            model.addAttribute("activePage", "subscriptions");
            return "subscription-form";
        } catch (Exception e) {
            return "redirect:/subscriptions";
        }
    }

    @PostMapping("/{id}/edit")
    public String update(@AuthenticationPrincipal User user,
                         @PathVariable UUID id,
                         @RequestParam String name,
                         @RequestParam BigDecimal amount,
                         @RequestParam String currency,
                         @RequestParam BillingCycle billingCycle,
                         @RequestParam LocalDate nextRenewalDate,
                         @RequestParam(required = false) Long categoryId,
                         @RequestParam(required = false) String notes,
                         @RequestParam(required = false) String website,
                         Model model) {
        try {
            subscriptionService.updateSubscription(user, id, new SubscriptionRequest(
                    categoryId, name, amount, currency,
                    billingCycle, nextRenewalDate, notes, website, null, false, null));
            return "redirect:/subscriptions";
        } catch (Exception e) {
            model.addAttribute("currentUser", user);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("billingCycles", BillingCycle.values());
            return "subscription-form";
        }
    }

    @PostMapping("/{id}/mark-paid")
    public String markPaid(@AuthenticationPrincipal User user,
                           @PathVariable UUID id) {
        subscriptionService.markPaid(user, id);
        return "redirect:/subscriptions";
    }

    @PostMapping("/{id}/mark-unpaid")
    public String markUnpaid(@AuthenticationPrincipal User user,
                             @PathVariable UUID id) {
        subscriptionService.markUnpaid(user, id);
        return "redirect:/subscriptions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal User user,
                         @PathVariable UUID id) {
        subscriptionService.deleteSubscription(user, id);
        return "redirect:/subscriptions";
    }
}