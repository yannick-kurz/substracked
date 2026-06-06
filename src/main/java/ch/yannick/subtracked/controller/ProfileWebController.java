package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.app.user.UserService;
import ch.yannick.subtracked.app.user.dto.ChangePasswordRequest;
import ch.yannick.subtracked.app.user.dto.UserProfileRequest;
import ch.yannick.subtracked.domain.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/profile")
public class ProfileWebController {

    private final UserService userService;

    public ProfileWebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "profile");
        return "profile";
    }

    @PostMapping("/update")
    public String update(@AuthenticationPrincipal User user,
                         @RequestParam String firstName,
                         @RequestParam String lastName,
                         @RequestParam String email,
                         Model model) {
        try {
            userService.updateUserProfile(user,
                    new UserProfileRequest(firstName, lastName, email));
            model.addAttribute("currentUser", user);
            model.addAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            model.addAttribute("currentUser", user);
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("activePage", "profile");
        return "profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal User user,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        try {
            userService.changePassword(user,
                    new ChangePasswordRequest(currentPassword, newPassword, confirmPassword));
            model.addAttribute("currentUser", user);
            model.addAttribute("passwordSuccess", "Password changed successfully");
        } catch (IllegalArgumentException e) {
            model.addAttribute("currentUser", user);
            model.addAttribute("passwordError", e.getMessage());
        }
        model.addAttribute("activePage", "profile");
        return "profile";
    }

    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal User user,
                         HttpServletRequest request) {
        // Session invalidieren bevor User gelöscht wird
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        userService.deleteUser(user);
        return "redirect:/auth/login";
    }
}