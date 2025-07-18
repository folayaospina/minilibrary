package com.mini_library.library.service.auth;

import com.mini_library.library.controller.auth.LoginRequest;
import com.mini_library.library.controller.auth.RegisterRequest;
import com.mini_library.library.controller.auth.TokenResponse;
import com.mini_library.library.interfaces.TokenType;
import com.mini_library.library.model.Role;
import com.mini_library.library.model.User;
import com.mini_library.library.model.auth.Token;
import com.mini_library.library.repository.RoleRepository;
import com.mini_library.library.repository.TokenRepository;
import com.mini_library.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public TokenResponse register(RegisterRequest request) {
        Role role = roleRepository.findRoleByRole(request.roleType())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .libraryId(request.libraryId())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(savedUser);
        var jwtTokenRefresh = jwtService.generateRefreshToken(savedUser);
        saveUserToken(savedUser, jwtToken);
        return new TokenResponse(jwtToken, jwtTokenRefresh);
    }

    public TokenResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            var user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Usuario no encontrado"
                    ));

            var jwtToken = jwtService.generateToken(user);
            var jwtTokenRefresh = jwtService.generateRefreshToken(user);
            revokeAllUserToken(user);
            saveUserToken(user, jwtToken);
            return new TokenResponse(jwtToken, jwtTokenRefresh);

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo electrónico o contraseña incorrectos"
            );
        }
    }

    private void revokeAllUserToken(final User user) {
        final List<Token> validUserTokens = tokenRepository
                .findAllValidIsFalseOrRevokedIsFalseByUser_Email(user.getEmail());
        if (!validUserTokens.isEmpty()) {
            for (final Token token : validUserTokens) {
                token.setRevoked(true);
                token.setExpired(true);
            }
            tokenRepository.saveAll(validUserTokens);
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }


    private TokenResponse refreshToken(final String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Bearer token");
        }

        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Refresh token");
        }

        final User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!jwtService.isValidToken(refreshToken, user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Refresh token");
        }

        final String accessToken = jwtService.generateToken(user);
        revokeAllUserToken(user);
        saveUserToken(user, accessToken);
        return new TokenResponse(accessToken, refreshToken);
    }

}