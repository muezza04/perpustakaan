package com.nuzurwan.perpustakaan.config;

import com.nuzurwan.perpustakaan.repository.UserRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;

    // Spring menjalankan method ini satu kali dan menyimpan hasilnya
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // untuk menghubungkan spring security dengan database kamu (menghindari confused)
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            return userRepository.findByEmail(email)
                    .map(user -> org.springframework.security.core.userdetails.User.builder()
                            .username(user.getEmail())
                            .password(user.getPassword())
                            .roles(user.getRole().name())
                            .build())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        };
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Perpustakaan")
                        .description("Note: Untuk Logout, kirimkan request **POST** ke `/api/auth/logout`. " +
                                "Ini akan menghapus JSESSIONID dari server."));
    }

    // Memberi tahu Spring Security untuk "mengabaikan" atau mengizinkan akses publik khusus untuk folder Swagger. Jika tidak, @Configutaion(default) akan mengunci semua endpoint api
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Nonaktifkan CSRF untuk keperluan testing API
                .authorizeHttpRequests(auth -> auth
                        // Izinkan akses publik untuk Swagger UI dan dokumentasi OpenAPI
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                // Sepertinya tidak wajib, cari tau kegunaannya
                                "/webjars/**"
                        // "/swagger-resources/**"
                        ).permitAll()
                        // Agar pintu masuk login terbuka untuk publik
                        .requestMatchers("/api/auth/login").permitAll()
                        // Semua request lainnya wajib login (atau bisa kamu sesuaikan)
                        .anyRequest().authenticated()
                )

                // Logout session, pahami code diatas, kalau bisa semua yg ada di package config sih
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                session.invalidate();
                            }
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK); // Mengirim status 200 OK
                        })
                )

                // Tetap aktifkan HTTP Basic agar Swagger bisa 'menembus' lewat tombol Authorize
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                }))

                .sessionManagement(session -> session
                        // Ubah menjadi ALWAYS jika IF_REQUIRED masih belum memancing Cookie keluar
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

                // Aktifkan Form Login agar muncul halaman login resmi Spring
                // .formLogin(Customizer.withDefaults())


        return http.build();
    }

}
