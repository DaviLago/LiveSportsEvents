package com.example.sports.exceptions;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${server.error.include-message:always}")
    private String includeMessage;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getDefaultMessage())
            .findFirst()
            .orElse("Validation error");
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, ex, request);
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message, Exception ex, WebRequest request) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        if ("always".equals(includeMessage)) {
            errorDetails.put("message", message);
        }
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(status).body(errorDetails);
    }

}