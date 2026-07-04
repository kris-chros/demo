package com.example.demo.rest;
import com.example.demo.DTO.AuthResponse;
import com.example.demo.DTO.CustomUser;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.service.impl.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        System.out.println(">>> CONTROLLER REACHED: Attempting login for " + request.username());
        // 1. Authenticate the user.
        // If the password doesn't match the database hash, this throws a BadCredentialsException.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // 2. Extract the authenticated user.
        // Assuming your UserDetailsService returns your CustomUser entity.
        CustomUser user = (CustomUser) authentication.getPrincipal();

        // 3. Generate the token containing the username and role.
        String token = jwtService.generateToken(user);

        // 4. Return the JWT to the client.
        return ResponseEntity.ok(new AuthResponse(token));
    }
}