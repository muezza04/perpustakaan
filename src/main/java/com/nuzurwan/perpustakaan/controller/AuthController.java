package com.nuzurwan.perpustakaan.controller;

import com.nuzurwan.perpustakaan.dto.request.LoginRequest;
import com.nuzurwan.perpustakaan.dto.response.LoginResponse;
import com.nuzurwan.perpustakaan.dto.response.WebResponse;
import com.nuzurwan.perpustakaan.service.AuthService;
// objek yang mewakili permintaan dari user. Di dalamnya terdapat semua informasi yang dikirimkan oleh browser (header, parameter, method, session access)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
// objek yang disimpan di memori server untuk mengenali user tertentu. Loker Pribadi
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoint untuk masuk dan keluar sistem. ")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<WebResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) { // <--- Ambil HttpServletRequest

        LoginResponse response = authService.login(request);

        // WAJIB: Membuat session secara manual agar Set-Cookie muncul di header
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("USER_SESSION", response);

        return ResponseEntity.ok(
                WebResponse.<LoginResponse>builder()
                        .message("Login Success")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout from system", description = "Endpoint ini ditangani oleh Spring Security. Kirim POST untuk menghapus session.")
    public ResponseEntity<WebResponse<String>> logoutPlaceholder() {
        return ResponseEntity.ok(WebResponse.<String>builder().message("Logout logic handled by Security").build());
    }
}
