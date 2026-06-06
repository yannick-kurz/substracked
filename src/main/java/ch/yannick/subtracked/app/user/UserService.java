package ch.yannick.subtracked.app.user;

import ch.yannick.subtracked.app.user.dto.ChangePasswordRequest;
import ch.yannick.subtracked.app.user.dto.UserProfileRequest;
import ch.yannick.subtracked.app.user.dto.UserResponse;
import ch.yannick.subtracked.domain.payment.PaymentRepository;
import ch.yannick.subtracked.domain.subscription.SubscriptionRepository;
import ch.yannick.subtracked.domain.user.RefreshTokenRepository;
import ch.yannick.subtracked.domain.user.User;
import ch.yannick.subtracked.domain.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository         userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository      paymentRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder        passwordEncoder;

    public UserService(UserRepository userRepository,
                       SubscriptionRepository subscriptionRepository,
                       PaymentRepository paymentRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository         = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository      = paymentRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder        = passwordEncoder;
    }

    public UserResponse getUserProfile(User user) {
        return new UserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public UserResponse updateUserProfile(User user, UserProfileRequest request) {
        user.setFirstName(request.firstname());
        user.setLastName(request.lastname());
        user.setEmail(request.email());
        userRepository.save(user);
        return getUserProfile(user);
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }
        if (request.newPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) {
        UUID userId = user.getId();
        refreshTokenRepository.deleteByUser(user);
        paymentRepository.deleteAllByUserId(userId);
        subscriptionRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}