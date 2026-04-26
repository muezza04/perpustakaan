package com.nuzurwan.perpustakaan.config;

import com.nuzurwan.perpustakaan.model.User;
import com.nuzurwan.perpustakaan.model.UserRole;
import com.nuzurwan.perpustakaan.model.UserStatus;
import com.nuzurwan.perpustakaan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@fool.com"; // Definisikan satu variabel agar konsisten

        try {
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = User.builder()
                        .email(adminEmail)
                        .firstName("Supreme")
                        .lastName("Admin")
                        .password(passwordEncoder.encode("admin444"))
                        .role(UserRole.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .build();

                userRepository.save(admin);
                System.out.println(">>> SEEDER: Default Admin Account Created: " + adminEmail);
            }
        } catch (Exception e) {
            System.err.println(">>> ERROR SEEDER: " + e.getMessage());
        }
    }
}