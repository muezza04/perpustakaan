package com.nuzurwan.perpustakaan.exception;

// Wrapper : untuk menjadikan 1 response ke json
import com.nuzurwan.perpustakaan.dto.response.WebResponse;
// Kebutuhan untuk status kode HTTP (seperti 200, 400, 404, 500)
import org.springframework.http.HttpStatus;
// Wadah untuk membungkus respon data, status, dan header HTTP
import org.springframework.http.ResponseEntity;
// Nama "Kecelakaan/Error" yang terjadi jika validasi DTO (@Valid) gagal
import org.springframework.web.bind.MethodArgumentNotValidException;
// Kesalahan/Error yang terjadi ketika User salah menggunakan Method (misal: harusnya POST tapi dia pakai GET).
import org.springframework.web.HttpRequestMethodNotSupportedException;
// Nama "Kecelakaan/Error" untuk logika bisnis (misal: data tidak ditemukan di Service)
import org.springframework.web.server.ResponseStatusException;
// Penanda (Anotasi) untuk menentukan method mana yang menangani error tertentu
import org.springframework.web.bind.annotation.ExceptionHandler;
// Penanda bahwa class ini adalah "Jaring Pengaman Global" untuk semua Controller
import org.springframework.web.bind.annotation.RestControllerAdvice;
// Memvalidasi masukan JSON structure, tipe data, choose enum
import org.springframework.http.converter.HttpMessageNotReadableException;

// Library standar Java untuk menyimpan data dalam bentuk Key (kunci) dan Value (isi)
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice // <-- Ini "Jaring Pengamannya" atau bisa dibilang menangkap segala error
public class GlobalExceptionHandler {

    // 1. MENANGKAP ERROR VALIDASI DTO (@NotBlank, @Size, dsb)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<String>> handleValidation(MethodArgumentNotValidException ex) {
        // Mengambil semua error dan menggabungkannya menjadi satu string yang rapi
        String allErrors = ex.getBindingResult().getFieldErrors().stream()
                // melihat error pada field error.getField() ; error.getDefaultMessage() mengambil pesan yg ada di dto request, jika tidak ada akan default message
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                // .collect(Collectors.joining(", ")) Semua teks di atas (digabungkan) menggunakan tanda koma dan spasi sebagai pemisahnya.
                .collect(Collectors.joining(", "));

        WebResponse<String> response = WebResponse.<String>builder()
                .message("Validasi Gagal")
                .data(null)
                .errors(allErrors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // 2. MENANGKAP ERROR LOGIKA (Service Level) (Misal: throw new ResponseStatusException di Service)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>> handleLogicError(ResponseStatusException ex) {
        WebResponse<String> response = WebResponse.<String>builder()
                .message("Failed to process the request")
                .data(null)
                .errors(ex.getReason()) // Mengambil pesan dari 'throw' Anda
                .build();

        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    // 3. MENANGKAP ERROR FORMAT JSON ATAU TIPE DATA
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<WebResponse<String>> handleJsonError(HttpMessageNotReadableException ex) {
        WebResponse<String> response = WebResponse.<String>builder()
                .message("Invalid JSON format")
                .data(null)
                .errors("Ensure that the JSON structure is correct and the data types are correct")
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // 4. MENANGKAP KESALAHAN METHOD HTTP (Salah pilih GET/POST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<WebResponse<String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("The '%s' method is not supported. Please use %s.",
                ex.getMethod(), ex.getSupportedHttpMethods());

        WebResponse<String> response = WebResponse.<String>builder()
                .message("Method Not Allowed")
                .data(null)
                .errors(message)
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    // 5. JARING PENGAMAN TERAKHIR (Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<String>> handleAllUncaughtErrors(Exception ex) {
        ex.printStackTrace(); // Tetap print untuk debugging kamu

        WebResponse<String> response = WebResponse.<String>builder()
                .message("Internal Server Error")
                .data(null)
                .errors("A system error has occurred: " + ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Untuk mengatasi error semua object null pada service
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<WebResponse<String>> handleNullPointer(NullPointerException ex) {

        WebResponse<String> response = WebResponse.<String>builder()
                .message("Internal Server Error")
                .errors("Terjadi kesalahan internal: Sistem mendeteksi adanya data kosong yang tidak seharusnya.")
                .data(null)
                .build();

        // Log detailnya di console agar kamu (Developer) bisa memperbaiki kodingannya
        // ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}