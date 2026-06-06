package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.domain.payment.Payment;
import ch.yannick.subtracked.domain.payment.PaymentRepository;
import ch.yannick.subtracked.domain.payment.PaymentResponse;
import ch.yannick.subtracked.domain.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/payments")
public class PaymentWebController {

    private final PaymentRepository paymentRepository;

    public PaymentWebController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal User user, Model model) {
        List<Payment> payments = paymentRepository
                .findByUserIdOrderByPaidOnDesc(user.getId());

        List<PaymentResponse> responses = payments.stream()
                .map(p -> new PaymentResponse(
                        p.getId(),
                        p.getSubscriptionId(),
                        p.getSubscriptionName(),
                        p.getAmount(),
                        p.getCurrency(),
                        p.getPaidOn()))
                .toList();

        // Monats-Totals
        LocalDate now = LocalDate.now();
        BigDecimal thisMonth = payments.stream()
                .filter(p -> p.getPaidOn().getMonth() == now.getMonth()
                        && p.getPaidOn().getYear()  == now.getYear())
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        LocalDate lastMonthDate = now.minusMonths(1);
        BigDecimal lastMonth = payments.stream()
                .filter(p -> p.getPaidOn().getMonth() == lastMonthDate.getMonth()
                        && p.getPaidOn().getYear()  == lastMonthDate.getYear())
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        model.addAttribute("currentUser", user);
        model.addAttribute("payments", responses);
        model.addAttribute("thisMonthTotal", thisMonth);
        model.addAttribute("lastMonthTotal", lastMonth);
        model.addAttribute("activePage", "payments");
        return "payments";
    }

    @Transactional
    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal User user,
                         @PathVariable UUID id) {
        paymentRepository.deleteByIdAndUserId(id, user.getId());
        return "redirect:/payments";
    }
}