package ch.yannick.subtracked.app.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}
