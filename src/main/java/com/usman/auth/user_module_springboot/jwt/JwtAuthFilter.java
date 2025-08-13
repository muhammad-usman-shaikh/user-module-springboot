package com.usman.auth.user_module_springboot.jwt;

import com.usman.auth.user_module_springboot.session.Session;
import com.usman.auth.user_module_springboot.session.SessionRepository;
import com.usman.auth.user_module_springboot.user.User;
import com.usman.auth.user_module_springboot.user.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService,
            SessionRepository sessionRepository,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Extract claims from token
            Claims claims = jwtService.extractAllClaims(token);

            Integer userId = Integer.valueOf(claims.getSubject());
            Integer sessionId = claims.get("sessionId", Integer.class);

            // Check session
            Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
            if (sessionOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid session");
                return;
            }

            Session session = sessionOpt.get();

            // Check refresh token expiry
            try {
                Claims refreshClaims = jwtService.extractAllClaims(session.getRefreshToken());
                if (refreshClaims.getExpiration().toInstant().isBefore(java.time.Instant.now())) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token expired");
                    return;
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid refresh token");
                return;
            }

            // Fetch user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            User user = userOpt.get();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                    user.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
