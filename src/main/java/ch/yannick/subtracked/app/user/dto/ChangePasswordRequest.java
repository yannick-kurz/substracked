package ch.yannick.subtracked.app.user.dto;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {}