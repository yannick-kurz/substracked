package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.domain.user.User;
import ch.yannick.subtracked.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SubscriptionWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("sub@test.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        userRepository.save(testUser);
    }


    @Test
    void postNewSubscription_authenticated_redirectsToSubscriptions() throws Exception {
        mockMvc.perform(post("/subscriptions/new")
                        .with(user(testUser))
                        .param("name", "Netflix")
                        .param("amount", "13.99")
                        .param("currency", "CHF")
                        .param("billingCycle", "MONTHLY")
                        .param("nextRenewalDate",
                                java.time.LocalDate.now().plusMonths(1).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/subscriptions"));
    }
}