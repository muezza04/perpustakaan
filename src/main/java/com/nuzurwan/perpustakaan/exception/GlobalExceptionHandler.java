package com.nuzurwan.perpustakaan.exception;

// Kebutuhan untuk status kode HTTP (seperti 200, 400, 404, 500)
import org.springframework.http.HttpStatus;
// Wadah untuk membungkus respon data, status, dan header HTTP
import org.springframework.http.ResponseEntity;
// Nama "Kecelakaan/Error" yang terjadi jika validasi DTO (@Valid) gagal
import org.springframework.web.bind.MethodArgumentNotValidException;
// Nama "Kecelakaan/Error" untuk logika bisnis (misal: data tidak ditemukan di Service)
import org.springframework.web.server.ResponseStatusException;
// Untuk mengambil detail kolom mana yang salah input (misal: kolom 'isbn')
import org.springframework.validation.FieldError;
// Penanda (Anotasi) untuk menentukan method mana yang menangani error tertentu
import org.springframework.web.bind.annotation.ExceptionHandler;
// Penanda bahwa class ini adalah "Jaring Pengaman Global" untuk semua Controller
import org.springframework.web.bind.annotation.RestControllerAdvice;
// Memvalidasi masukan JSON structure, tipe data, choose enum
import org.springframework.http.converter.HttpMessageNotReadableException;

// Library standar Java untuk menyimpan data dalam bentuk Key (kunci) dan Value (isi)
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

    // 3. JARING PENGAMAN TERAKHIR: Menangkap kesalahan sistem/bug yang tidak terduga (Status 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllUncaughtErrors(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Terjadi kesalahan internal pada server. Silakan hubungi admin.");

        // Penting untuk developer melihat apa yang salah di console
        ex.printStackTrace();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Menangani error jika format JSON yang dikirim client rusak atau
    // tipe data tidak sesuai (misal: input string ke variabel Integer dll).
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonError(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();

        // fokus hanya pada kesalahan struktur JSON secara umum
        error.put("error", "Format JSON tidak valid. Pastikan tanda koma, tanda petik, dan tipe data sudah benar.");

        return ResponseEntity.badRequest().body(error);
    }
}