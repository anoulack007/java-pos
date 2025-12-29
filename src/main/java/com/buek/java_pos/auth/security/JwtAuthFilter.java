package com.buek.java_pos.auth.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.MDC;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);

        try {
            String username = jwtService.parseToken(token)
                    .getBody()
                    .getSubject();

            
            MDC.put("user", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);

            }

        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid or expired token\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
