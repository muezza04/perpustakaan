package com.nuzurwan.perpustakaan.service;

import com.nuzurwan.perpustakaan.dto.request.LoginRequest;
import com.nuzurwan.perpustakaan.dto.response.LoginResponse;
import com.nuzurwan.perpustakaan.dto.response.UserResponse;
import com.nuzurwan.perpustakaan.model.User;
import com.nuzurwan.perpustakaan.model.UserStatus;
import com.nuzurwan.perpustakaan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // anotasi ini berfungsi untuk menjamin Integritas Data, membungkus seluruh perintah di dalam method menjadi satu kesatuan.
    @Transactional(readOnly = true) // method hanya melakukan read saja
    public LoginResponse login(LoginRequest request) {
        // 1. Cari user berdasarkan email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email address or password"));

        // 2. Validasi Password (Plain text vs Hash di DB)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email address or password");
        }

        // 3. Cek apakah akun aktif
        if (user.getStatus() == UserStatus.DEACTIVATED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account blocked. Please contact the administrator.");
        }

        // 4. Return LoginResponse (Data ini nanti digunakan Controller untuk isi Session)
        return LoginResponse.builder()
                .user(UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .fullName(user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : ""))
                        .role(user.getRole())
                        .status(user.getStatus())
                        .build())
                .build();
    }
}
