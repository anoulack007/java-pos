package com.buek.java_pos.config;

import com.buek.java_pos.common.ApiError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final ObjectMapper om = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.INDENT_OUTPUT);

    private String requestId() {
        return MDC.get("requestId");
    }

    private ApiError build(HttpStatus status, String message, HttpServletRequest req) {
        return new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                requestId());
    }

    private void logApiWarn(ApiError err) {
        try {
            log.warn(om.writeValueAsString(err));
        } catch (JsonProcessingException e) {
            log.warn("{}", err);
        }
    }

    private void logApiError(ApiError err, Exception ex) {
        try {
            log.error(om.writeValueAsString(err), ex);
        } catch (JsonProcessingException e) {
            log.error("{}", err, ex);
        }
    }

    // ✅ Validation error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("validation error");

        ApiError err = build(HttpStatus.BAD_REQUEST, msg, req);
        logApiWarn(err);

        return ResponseEntity.badRequest().body(err);
    }

    // ✅ JSON parse error
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonParse(HttpMessageNotReadableException ex, HttpServletRequest req) {

        ApiError err = build(HttpStatus.BAD_REQUEST, "Invalid JSON request body", req);
        logApiWarn(err);

        return ResponseEntity.badRequest().body(err);
    }

    // ✅ Bad request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {

        ApiError err = build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        logApiWarn(err);

        return ResponseEntity.badRequest().body(err);
    }

    // ✅ Auth fail (401)
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiError> handleAuthFail(Exception ex, HttpServletRequest req) {

        ApiError err = build(HttpStatus.UNAUTHORIZED, "Invalid username or password", req);
        logApiWarn(err);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    // ✅ fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest req) {

        ApiError err = build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", req);
        logApiError(err, ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
