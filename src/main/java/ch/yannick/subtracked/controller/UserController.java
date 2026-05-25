package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.app.user.UserService;
import ch.yannick.subtracked.app.user.dto.UserProfileRequest;
import ch.yannick.subtracked.app.user.dto.UserResponse;
import ch.yannick.subtracked.domain.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/userprofile")
    public ResponseEntity<UserResponse> getUser(
            @AuthenticationPrincipal User user ) {
        return ResponseEntity.ok(service.getUserProfile(user));
    }

    @PutMapping("/update-user")
    public ResponseEntity<UserResponse> updateUserProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserProfileRequest request
            ) {
        return ResponseEntity.ok(service.updateUserProfile(user, request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal User user ) {
        service.deleteUser(user);
        return ResponseEntity.noContent().build();
    }
}
