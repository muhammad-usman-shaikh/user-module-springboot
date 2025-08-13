package com.usman.auth.user_module_springboot.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.usman.auth.user_module_springboot.user.UserRole;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    @Value("${access.token.expiry.seconds}")
    private long accessTokenExpirySeconds;

    @Value("${refresh.token.expiry.seconds}")
    private long refreshTokenExpirySeconds;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT access token for a user session.
     *
     * @param sessionId the ID of the user session
     * @param userId    the ID of the user
     * @param role      the role of the user
     * @return a JWT access token string
     */
    public String generateAccessToken(Integer sessionId, Integer userId, UserRole role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (accessTokenExpirySeconds * 1000));

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("sessionId", sessionId)
                .claim("role", role)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT refresh token for a user session.
     *
     * @param userId the ID of the user
     * @param role   the role of the user
     * @return a JWT refresh token string
     */
    public String generateRefreshToken(Integer userId, UserRole role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (refreshTokenExpirySeconds * 1000));
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("role", role)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
