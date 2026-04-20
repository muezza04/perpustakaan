package com.nuzurwan.perpustakaan.config;

import com.nuzurwan.perpustakaan.model.User;
import com.nuzurwan.perpustakaan.model.UserRole;
import com.nuzurwan.perpustakaan.model.UserStatus;
import com.nuzurwan.perpustakaan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // try-catch di dalam DatabaseSeeder untuk melihat apakah ada error saat proses insert admin.
        try {
            // Cek apakah sudah ada admin di database
            if (userRepository.findByEmail("admin@perpustakaan.com").isEmpty()) {
                User admin = User.builder()
                        .email("admin@fool.com")
                        .firstName("Supreme")
                        .lastName("Admin")
                        .password(passwordEncoder.encode("admin444")) // Wajib di-encode!
                        .role(UserRole.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .build();

                userRepository.save(admin);
                System.out.println(">>> WARNING: Default Admin Account Created!");
            }
        } catch (Exception e) {
            System.err.println(">>> ERROR SEEDER: " + e.getMessage());
        }
    }
}
