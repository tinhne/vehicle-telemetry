package com.vehicletelemetry.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import java.time.Instant;
import java.util.Map;

/**
 * GlobalExceptionHandler — turns all unhandled exceptions into structured JSON.
 *
 * Without this: Spring returns an HTML Whitelabel error page — terrible for Android clients.
 * With this: every error returns a clean JSON envelope that Retrofit can parse.
 *
 * NEVER include stack traces in response bodies — they expose internal details.
 * Log the full exception server-side; return only a safe generic message to clients.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> badRequest(
            IllegalArgumentException ex, WebRequest req) {
        log.warn("Bad request: {}", ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> internal(
            Exception ex, WebRequest req) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error occurred", req);
    }

    private ResponseEntity<Map<String, Object>> error(
            HttpStatus status, String msg, WebRequest req) {
        return ResponseEntity.status(status).body(Map.of(
            "status",    status.value(),
            "error",     status.getReasonPhrase(),
            "message",   msg,
            "timestamp", Instant.now().toString(),
            "path",      req.getDescription(false).replace("uri=", "")
        ));
    }
}
