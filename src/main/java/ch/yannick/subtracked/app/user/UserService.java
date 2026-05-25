package ch.yannick.subtracked.app.user;

import ch.yannick.subtracked.app.user.dto.UserProfileRequest;
import ch.yannick.subtracked.app.user.dto.UserResponse;
import ch.yannick.subtracked.domain.user.User;
import ch.yannick.subtracked.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
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

        repository.save(user);
        return getUserProfile(user);
    }

    @Transactional
    public void deleteUser(User user) {
        repository.delete(user);
    }
}
