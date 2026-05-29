package ch.yannick.subtracked.app.config;

import ch.yannick.subtracked.app.subscription.SubscriptionService;
import ch.yannick.subtracked.domain.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final SubscriptionService subscriptionService;

    public GlobalModelAdvice(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @ModelAttribute("upcoming")
    public Object upcoming(@AuthenticationPrincipal User user) {
        if (user == null) return null;
        return subscriptionService.getUpcomingRenewals(user, 7);
    }
}