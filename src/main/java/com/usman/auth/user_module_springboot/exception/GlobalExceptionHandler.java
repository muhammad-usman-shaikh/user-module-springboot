package com.usman.auth.user_module_springboot.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError firstError = ex.getBindingResult().getFieldErrors().get(0);

        Map<String, String> response = new HashMap<>();
        response.put("message", firstError.getDefaultMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Duplicate value";

        // Try to extract the field from the SQL exception message
        String causeMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";
        System.err.println("Cause message: " + causeMsg);
        System.err.println("\n\n\n");

        if (causeMsg.contains("unique_email")) {
            message = "email already exists";
        } else if (causeMsg.contains("unique_username")) {
            message = "username already exists";
        } // add more fields as needed

        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
