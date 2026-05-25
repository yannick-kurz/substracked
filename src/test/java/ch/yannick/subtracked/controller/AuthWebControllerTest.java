package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.domain.user.User;
import ch.yannick.subtracked.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void getLoginPage_returns200() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    void getRegisterPage_returns200() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk());
    }

    @Test
    void postRegister_createsUserAndRedirects() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("firstName", "Yannick")
                        .param("lastName", "Test")
                        .param("email", "yannick@test.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void postLogin_withValidCredentials_redirectsToDashboard() throws Exception {
        User user = new User();
        user.setFirstName("Yannick");
        user.setLastName("Test");
        user.setEmail("login@test.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        mockMvc.perform(post("/auth/login")
                        .param("email", "login@test.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void postLogin_withWrongPassword_returnsLoginPage() throws Exception {
        User user = new User();
        user.setFirstName("Yannick");
        user.setLastName("Test");
        user.setEmail("wrong@test.com");
        user.setPassword(passwordEncoder.encode("correct"));
        userRepository.save(user);

        mockMvc.perform(post("/auth/login")
                        .param("email", "wrong@test.com")
                        .param("password", "falsch"))
                .andExpect(status().isOk());
    }
}