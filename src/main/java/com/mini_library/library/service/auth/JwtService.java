package com.mini_library.library.service.auth;

import com.mini_library.library.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {


    @Value("${application.security.jwt.secret}")
    private String secretKey;
    @Value("${application.security.jwt.expirationMs}")
    private Long jwtExpiration;
    @Value("${application.security.jwt.refreshExpirationMs}")
    private Long jwtRefreshExpiration;

    public boolean isValidToken(final String token, final User user){
        final String userEmail = extractUsername(token);
        return userEmail != null && userEmail.equals(user.getEmail()) &&!isTokenExpired(token);
    }

    private boolean isTokenExpired(final String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(final String token){
        final Claims jwtToken =  Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return jwtToken.getExpiration();
    }
    public  String extractUsername(final String token){
        final Claims jwtToken = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return jwtToken.getSubject();
    }

    public String generateToken(final User user){
        return buildToken(user, jwtExpiration);
    }
    public String generateRefreshToken(final User user){
        return buildToken(user, jwtRefreshExpiration);
    }

    private String buildToken (final User user, final long expiration){
        return Jwts.builder()
                .setClaims(Map.of("email", user.getEmail().toString()))
                .setSubject(user.getEmail())
                .setId(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
