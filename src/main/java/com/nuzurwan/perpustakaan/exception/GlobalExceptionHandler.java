package com.nuzurwan.perpustakaan.exception;

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

@RestControllerAdvice // <-- Ini "Jaring Pengamannya" atau bisa dibilang menangkap segala error
public class GlobalExceptionHandler {

    // MENANGKAP ERROR VALIDASI(DTO) (Misal: @NotBlank, @Min, @Size di DTO)
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

    // MENANGKAP ERROR LOGIKA (Misal: throw new ResponseStatusException di Service)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleLogicError(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getReason()); // Mengambil pesan dari 'throw' Anda

        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    // JARING PENGAMAN TERAKHIR: Menangkap kesalahan sistem/bug yang tidak terduga (Status 500)
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

        // Memberikan pesan umum dimengerti user
        String message = "Format data tidak valid.";

        // Opsional: mengambil sedikit info dari ex tanpa membocorkan rahasia sistem
        if (ex.getMessage().contains("Cannot deserialize")) {
            message = "Tipe data tidak sesuai. Pastikan angka dan teks diletakkan di kolom yang benar.";
        }

        error.put("error", message);
        error.put("details", "Pastikan struktur JSON benar (tanda koma, petik, dan kurung kurawal).");

        return ResponseEntity.badRequest().body(error);
    }

    // Untuk menerima error pada null yg terutama berada di dto request dan service(parameter method)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNullPointer(NullPointerException ex) {
        Map<String, String> error = new HashMap<>();

        // Memberikan pesan yang lebih profesional dan aman
        error.put("error", "Terjadi kesalahan internal pada pemrosesan data.");
        error.put("message", "Sistem mendeteksi adanya data kosong yang tidak seharusnya. Silakan hubungi admin.");

        // Log error secara internal di console agar kamu mudah menelusurinya saat koding
        // ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // untuk menangkap error kesalahan dalam memilih method (pilih GET sebenarnya method POST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        Map<String, String> error = new HashMap<>();

        // ex.getMethod() mengambil method yang salah (misal: GET)
        // ex.getSupportedHttpMethods() mengambil method yang seharusnya (misal: POST)
        String message = String.format("Method '%s' tidak didukung untuk endpoint ini. Silakan gunakan method %s",
                ex.getMethod(),
                ex.getSupportedHttpMethods());

        error.put("error", "Method Not Allowed");
        error.put("message", message);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }
}