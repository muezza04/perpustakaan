package com.nuzurwan.perpustakaan.controller;

import com.nuzurwan.perpustakaan.dto.request.LoginRequest;
import com.nuzurwan.perpustakaan.dto.response.LoginResponse;
import com.nuzurwan.perpustakaan.dto.response.WebResponse;
import com.nuzurwan.perpustakaan.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login user for get cookie")
    public ResponseEntity<WebResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        // 1. Eksekusi Login & Registrasi Security Context di Service
        LoginResponse response = authService.login(request);

        // 2. Kelola Session Persistence (Urusan Controller)
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok(
                WebResponse.<LoginResponse>builder()
                        .message("Login Success")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout from system",
            description = "Endpoint ini ditangani secara otomatis oleh Spring Security Filter. " +
                    "Memanggil ini akan menghapus session di server dan kuki di browser."
    )
    public ResponseEntity<WebResponse<String>> logoutPlaceholder() {
        // Baris ini secara teknis tidak akan dieksekusi karena dipotong oleh SecurityConfig,
        // namun harus tetap valid secara sintaksis untuk keperluan dokumentasi Swagger.
        return ResponseEntity.ok(
                WebResponse.<String>builder()
                        .message("Logout logic handled by Security")
                        .data("OK")
                        .build()
        );
    }
}