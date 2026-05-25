package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.app.subscription.SubscriptionService;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionRequest;
import ch.yannick.subtracked.app.subscription.dto.SubscriptionResponse;
import ch.yannick.subtracked.domain.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SubscriptionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createSubscription(user, request));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getAll(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getAllSubscriptions(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> getById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(service.getSubscriptionById(user, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(service.updateSubscription(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        service.deleteSubscription(user, id);
        return ResponseEntity.noContent().build();
    }
}