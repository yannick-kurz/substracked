package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.domain.user.User;
import ch.yannick.subtracked.domain.user.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile")
public class ProfileWebController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileWebController(UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            userRepository.save(user);
            model.addAttribute("currentUser", user);
            model.addAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            model.addAttribute("currentUser", user);
            model.addAttribute("error", e.getMessage());
        }
        return "profile";
    }

    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal User user) {
        userRepository.delete(user);
        return "redirect:/auth/login";
    }
}