package com.example.demo.rest;

import com.example.demo.DTO.CustomUser;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.config.JwtAuthFilter;
import com.example.demo.config.SecurityConfig;
import com.example.demo.service.impl.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, AuthControllerTest.TestSecurityConfig.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Provided by Spring Boot to map objects to JSON

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfig {
    }

    @BeforeEach
    void setUpFilterChain() throws Exception {
        // Prevents the mocked JwtAuthFilter from halting the request before it hits the controller.
        Mockito.doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    @Test
    void login_withValidCredentials_returnsToken() throws Exception {
        // 1. Arrange: Setup inputs and expected outputs
        LoginRequest loginRequest = new LoginRequest("testuser", "correctPassword");
        CustomUser mockUser = Mockito.mock(CustomUser.class);
        Authentication mockAuth = Mockito.mock(Authentication.class);
        String expectedToken = "mock.jwt.token.12345";

        // 2. Mock Behavior: Simulate a successful authentication flow
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        Mockito.when(mockAuth.getPrincipal()).thenReturn(mockUser);

        Mockito.when(jwtService.generateToken(mockUser)).thenReturn(expectedToken);

        // 3. Act & Assert: Execute request and verify response
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))) // Convert object to JSON
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));
    }

    @Test
    void login_withInvalidCredentials_fails() throws Exception {
        // 1. Arrange: Setup invalid inputs
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongPassword");

        // 2. Mock Behavior: Simulate AuthenticationManager throwing an exception
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        // 3. Act & Assert: Expect a 401 Unauthorized (or 403, depending on your SecurityConfig)
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isForbidden()); // Change to isForbidden() if your app returns 403 here
    }
}
