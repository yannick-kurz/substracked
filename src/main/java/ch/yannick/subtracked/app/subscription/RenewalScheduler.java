package ch.yannick.subtracked.app.subscription;

import ch.yannick.subtracked.domain.subscription.Subscription;
import ch.yannick.subtracked.domain.subscription.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RenewalScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(RenewalScheduler.class);

    private final SubscriptionRepository repository;

    public RenewalScheduler(SubscriptionRepository repository) {
        this.repository = repository;
    }

    // Läuft täglich um 08:00
    @Scheduled(cron = "0 0 8 * * *")
    public void checkUpcomingRenewals() {
        LocalDate in3Days = LocalDate.now().plusDays(3);

        List<Subscription> due = repository.findByNextRenewalDateAndActiveTrue(in3Days);

        if (due.isEmpty()) {
            log.info("RenewalScheduler: no renewals due in 3 days");
            return;
        }

        due.forEach(sub ->
                log.info("RenewalScheduler: {} for user {} due on {}",
                        sub.getName(),
                        sub.getUser().getId(),
                        sub.getNextRenewalDate()));

        // Hier später Email oder Push-Notification einbauen
    }
}