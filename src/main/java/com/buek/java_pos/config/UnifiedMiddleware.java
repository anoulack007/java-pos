package com.buek.java_pos.config;

import com.buek.java_pos.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Component
public class UnifiedMiddleware extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(UnifiedMiddleware.class);
    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_USER = "user";
    private static final String HEADER_REQUEST_ID = "X-Request-Id";

    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Setup RequestId
        String requestId = request.getHeader(HEADER_REQUEST_ID);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(MDC_REQUEST_ID, requestId);
        request.setAttribute(MDC_REQUEST_ID, requestId);
        response.setHeader(HEADER_REQUEST_ID, requestId);

        // 2. Log request
        logger.info("Method: {} | Path: {} | Remote: {}", 
            request.getMethod(), 
            request.getRequestURI(), 
            request.getRemoteAddr());

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        try {
            filterChain.doFilter(request, wrappedResponse);
        } finally {
            // 3. Log response
            logger.info("Method: {} | Path: {} | Status: {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                wrappedResponse.getStatus());

            // 4. Wrap successful responses
            if (wrappedResponse.getStatus() >= 200 && wrappedResponse.getStatus() < 300) {
                byte[] content = wrappedResponse.getContentAsByteArray();
                Object data = null;
                
                if (content.length > 0) {
                    try {
                        String body = new String(content, StandardCharsets.UTF_8);
                        data = objectMapper.readValue(body, Object.class);
                    } catch (Exception e) {
                        // If parsing fails, use null
                    }
                }
                
                ApiResponse<?> apiResponse = new ApiResponse<>(
                    Instant.now(),
                    wrappedResponse.getStatus(),
                    "Success",
                    data,
                    request.getRequestURI(),
                    MDC.get(MDC_REQUEST_ID)
                );
                
                String wrappedBody = objectMapper.writeValueAsString(apiResponse);
                logger.info("\u001B[32m{}\u001B[0m", wrappedBody);
                wrappedResponse.resetBuffer();
                wrappedResponse.getWriter().write(wrappedBody);
            }
            
            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }
}
