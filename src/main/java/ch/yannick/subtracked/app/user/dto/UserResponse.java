package ch.yannick.subtracked.app.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
        String firstname,
        String lastname,
        String email,
        LocalDateTime createdAt
) {}