package com.nuzurwan.perpustakaan.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError; // error yg ada dalam column pengisian data input
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler; // anotasi untuk memberitahu error apa
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException; // error logika ex: service

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // <-- Ini "Jaring Pengamannya"
public class GlobalExceptionHandler {

    // 1. MENANGKAP ERROR VALIDASI (Misal: @NotBlank, @Min, @Size di DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    // 2. MENANGKAP ERROR LOGIKA (Misal: throw new ResponseStatusException di Service)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleLogicError(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getReason()); // Mengambil pesan dari 'throw' Anda

        return new ResponseEntity<>(error, ex.getStatusCode());
    }
}