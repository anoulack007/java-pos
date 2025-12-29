package com.buek.java_pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import com.buek.java_pos.common.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.MDC;
import java.time.Instant;
import org.springframework.http.HttpStatus;

import com.buek.java_pos.auth.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {
    private final UnifiedMiddleware unifiedMiddleware;
    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.INDENT_OUTPUT);

    public SecurityConfig(UnifiedMiddleware unifiedMiddleware) {
        this.unifiedMiddleware = unifiedMiddleware;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    private String buildErrorResponse(int status, String message) throws Exception {
        ApiError err = new ApiError(
            Instant.now(),
            status,
            HttpStatus.valueOf(status).getReasonPhrase(),
            message,
            "",
            MDC.get("requestId")
        );
        return objectMapper.writeValueAsString(err);
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        JwtAuthFilter jwtAuthFilter
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> 
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()   
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, exception) -> {
                    try {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write(buildErrorResponse(403, "Access denied"));
                    } catch (Exception e) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    }
                })
                .authenticationEntryPoint((request, response, exception) -> {
                    try {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write(buildErrorResponse(401, "Unauthorized"));
                    } catch (Exception e) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                })
            )
            .addFilterBefore(unifiedMiddleware, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
    }
}
