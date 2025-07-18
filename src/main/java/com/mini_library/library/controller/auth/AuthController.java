package com.mini_library.library.controller.auth;

import com.mini_library.library.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    /**
     * Handles the user registration process by accepting a registration request,
     * creating a new user, and generating authentication tokens for the registered user.
     *
     * @param request the registration request containing user details such as name,
     *                email, library ID, password, and role type (ADMIN; USER).
     * @return a ResponseEntity containing a TokenResponse object with an access token
     *         and a refresh token.
     */
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody final RegisterRequest request) {
        final TokenResponse token = service.register(request);
        return ResponseEntity.ok(token);
    }

    /**
     * Authenticates a user based on the provided login credentials and returns a token response
     * containing access and refresh tokens.
     *
     * @param request the login request containing email and password of the user
     * @return a ResponseEntity containing the token response with access and refresh tokens
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody final LoginRequest request) {
        final TokenResponse token = service.login(request);
        return ResponseEntity.ok(token);
    }

    /**
     * Refreshes the access token using a provided refresh token in the Authorization header.
     *
     * @param authHeader The Authorization header containing the Bearer token. It should start with "Bearer " followed by the refresh token.
     * @return A ResponseEntity containing a TokenResponse object, which includes the new access token and the refresh token.
     *         If the refresh token is invalid, expired, or if the user is not found, appropriate HTTP error responses are returned.
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        return null;
    }
}
