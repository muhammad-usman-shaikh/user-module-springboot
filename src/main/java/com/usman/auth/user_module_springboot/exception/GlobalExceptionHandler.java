package com.usman.auth.user_module_springboot.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.usman.auth.user_module_springboot.dto.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError firstError = ex.getBindingResult().getFieldErrors().get(0);

        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(firstError.getDefaultMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle DataIntegrityViolationException, which is thrown when a duplicate
     * value
     * is attempted to be inserted into a column with a unique constraint.
     *
     * If the exception message contains "unique_email", return a 409 (Conflict)
     * response with a message "email already exists". If it contains
     * "unique_username", return a 409 response with a message "username already
     * exists". Add more fields as needed.
     *
     * @param ex The DataIntegrityViolationException to handle
     * @return A ResponseEntity containing a Map with the message
     */
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

    /**
     * Handles {@link InvalidCredentialsException} by returning a 400 Bad Request
     * response
     * with a JSON object containing the message from the exception.
     *
     * @param ex The exception to handle
     * @return A 400 Bad Request response with a JSON body
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
