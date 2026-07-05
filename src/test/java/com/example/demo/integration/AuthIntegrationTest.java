package com.example.demo.integration;

import com.example.demo.DTO.CustomUser;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Loads the full Spring Boot application context
@SpringBootTest
// Enables MockMvc so we can simulate HTTP requests without starting a real web server
@AutoConfigureMockMvc
// Automatically rolls back database transactions after each test finishes
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // 1. Prepare the real database state
        // We use the password encoder to ensure the password is hashed exactly as it would be in production
        CustomUser user = new CustomUser();
        user.setUsername("integration_user");
        user.setPassword(passwordEncoder.encode("supersecret123"));
        user.setRole("USER");
        // Note: You may need to set a role/authority here depending on your CustomUser entity
        // user.setRole("ROLE_USER");

        userRepository.save(user);
    }

    @Test
    void fullLoginFlow_withValidCredentials_returnsRealJwt() throws Exception {
        // 2. Setup the request payload
        LoginRequest request = new LoginRequest("integration_user", "supersecret123");

        // 3. Act & Assert: Send the request through the entire stack
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                // Verify the response actually contains a token field
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void fullLoginFlow_withInvalidPassword_returnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("integration_user", "wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // Depending on your ExceptionHandling config, this might be isForbidden() instead
                .andExpect(status().isForbidden());
    }
}