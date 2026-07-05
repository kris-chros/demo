package com.example.demo.service;

import com.example.demo.DTO.CustomUser;
import com.example.demo.service.impl.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Since JwtService has no external dependencies (like repositories),
        // we can simply instantiate a real version of it for testing.
        jwtService = new JwtService("MySuperSecretKeyThatIsAtLeast32BytesLong123!");
    }

    @Test
    void generateToken_createsValidJwtFormat() {
        // 1. Arrange: Create a mock user with a specific username and role
        CustomUser mockUser = Mockito.mock(CustomUser.class);
        Mockito.when(mockUser.getUsername()).thenReturn("testuser");

        // Set up the mock to return a collection containing one GrantedAuthority
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        // We use doReturn instead of when().thenReturn() to avoid generic type warnings with Collections
        Mockito.doReturn(authorities).when(mockUser).getAuthorities();

        // 2. Act: Generate the token
        String token = jwtService.generateToken(mockUser);

        // 3. Assert: A valid JWT consists of 3 parts separated by dots (Header.Payload.Signature)
        assertNotNull(token);
        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length, "Token should have 3 parts separated by dots");
    }

    @Test
    void extractAllClaims_parsesTokenAndReturnsCorrectData() {
        // 1. Arrange: First, we need to generate a real token to test the extraction logic
        CustomUser mockUser = Mockito.mock(CustomUser.class);
        Mockito.when(mockUser.getUsername()).thenReturn("testuser");

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Mockito.doReturn(authorities).when(mockUser).getAuthorities();

        String validToken = jwtService.generateToken(mockUser);

        // 2. Act: Extract the claims from the generated token
        Claims claims = jwtService.extractAllClaims(validToken);

        // 3. Assert: Verify the Subject (username) and Custom Claim (role) are correctly parsed
        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject(), "Subject should match the username");
        assertEquals("ROLE_ADMIN", claims.get("role"), "Role claim should match the granted authority");

        // Ensure expiration and issued at dates exist
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());

        // Ensure expiration is after issued time
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }
}
