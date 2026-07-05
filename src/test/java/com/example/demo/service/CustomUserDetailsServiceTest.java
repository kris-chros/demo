package com.example.demo.service;

import com.example.demo.DTO.CustomUser;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    // @Mock creates a fake version of the repository
    @Mock
    private UserRepository userRepository;

    // @InjectMocks creates an instance of our service and automatically injects the mocked repository into it
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        // 1. Arrange: Setup the expected username and mock the repository response
        String username = "testuser";

        // We mock CustomUser to avoid needing to know exactly how your entity is constructed
        CustomUser mockUser = Mockito.mock(CustomUser.class);
        Mockito.when(mockUser.getUsername()).thenReturn(username);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // 2. Act: Call the method being tested
        UserDetails result = customUserDetailsService.loadUserByUsername(username);

        // 3. Assert: Verify the result is what we expect
        assertNotNull(result);
        assertEquals(username, result.getUsername());

        // Verify that the repository was actually called with the correct username
        Mockito.verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_throwsException() {
        // 1. Arrange: Setup the repository to return an empty Optional
        String username = "unknownuser";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // 2. Act & Assert: Verify that calling the method throws the expected exception
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username)
        );

        // Verify the exception message matches exactly what you wrote in the service
        assertEquals("User not found", exception.getMessage());
        Mockito.verify(userRepository).findByUsername(username);
    }
}
