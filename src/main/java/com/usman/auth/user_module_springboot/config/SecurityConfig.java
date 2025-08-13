package com.usman.auth.user_module_springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/register").permitAll()
                        .requestMatchers("/api/v1/users/verify-email").permitAll()
                        .requestMatchers("/api/v1/users/login").permitAll()
                        .anyRequest().authenticated() // Everything else requires auth
                )
                .httpBasic(Customizer.withDefaults()); // Or jwt/login config
        return http.build();
    }
}
