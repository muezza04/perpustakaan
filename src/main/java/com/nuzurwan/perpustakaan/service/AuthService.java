package com.nuzurwan.perpustakaan.service;

import com.nuzurwan.perpustakaan.dto.request.LoginRequest;
import com.nuzurwan.perpustakaan.dto.response.LoginResponse;
import com.nuzurwan.perpustakaan.dto.response.UserResponse;
import com.nuzurwan.perpustakaan.model.User;
import com.nuzurwan.perpustakaan.model.UserStatus;
import com.nuzurwan.perpustakaan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // 1. Cari user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email address or password"));

        // 2. Validasi Password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email address or password");
        }

        // 3. Cek Status
        if (user.getStatus() == UserStatus.DEACTIVATED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account blocked.");
        }

        // 4. DAFTARKAN KE SECURITY CONTEXT (Pindahan dari Controller)
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 5. Kembalikan Response menggunakan helper mapping
        return LoginResponse.builder()
                .user(toUserResponse(user))
                .build();
    }

    // HELPER METHOD: Agar logika mapping tidak mengotori method utama
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : ""))
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}