package com.nuzurwan.perpustakaan.config;

import com.nuzurwan.perpustakaan.dto.response.WebResponse;
import com.nuzurwan.perpustakaan.exception.CustomAuthenticationEntryPoint;
import com.nuzurwan.perpustakaan.repository.UserRepository;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // untuk mengaktifkan keamanan berbasis web pada aplikasi Spring Boot.
@EnableMethodSecurity // Wajib agar @PreAuthorize aktif
public class SecurityConfig {
    private final UserRepository userRepository;

    // Spring menjalankan method ini satu kali dan menyimpan hasilnya
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        // Menggunakan pemisah '\n' untuk kejelasan
        String hierarchy = "ROLE_ADMIN > ROLE_LIBRARIAN \n" +
                "ROLE_LIBRARIAN > ROLE_MEMBER \n" +
                "ROLE_MEMBER > ROLE_GUEST";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
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
                .info(new Info().title("API Perpustakaan").version("1.0").description("Sistem Informasi Perpustakaan, tujuan dari sistem ini adalah penerjemah API untuk semua platform, dan juga bisa menjadi data pusat(hub)"))
                // DAFTARKAN URUTAN DI SINI, Pastikan .name nya sama case-sensitif dengan yg ada di anotation Tag di setiap file controller
                .addTagsItem(new Tag().name("Authentication").description("Endpoint for Login dan Logout ('/api/auth/logout' -> use Method 'POST')"))
                .addTagsItem(new Tag().name("User Controller").description("Manajemen data Pengguna"))
                .addTagsItem(new Tag().name("Book Controller").description("Manajemen data Buku Perpustakaan"))
                .addTagsItem(new Tag().name("Loan Controller").description("Manajemen data Loan Perpustakaan"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("CookieAuth", new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.APIKEY)
                                .in(io.swagger.v3.oas.models.security.SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")));

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
                        .requestMatchers(HttpMethod.POST,"/api/user").permitAll() // open for register user
                        .requestMatchers("/api/auth/login").permitAll() // all api di buka sementara

                        // Contoh: Hanya ADMIN yang bisa menghapus user
                        // Bisa menggunakan HttpMethod.GET
                         .requestMatchers("/api/**").hasRole("ADMIN")

                        // Semua request lainnya wajib login (atau bisa kamu sesuaikan)
                        .anyRequest().authenticated()
                )

                // handler error ke package exception
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )

                // Logout session, pahami code diatas, kalau bisa semua yg ada di package config sih
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Best Practice: Hapus kuki secara eksplisit saat logout sukses
                            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("JSESSIONID", null);
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);

                            // Sehingga data response identik semuanya
                            WebResponse<String> webResponse = WebResponse.<String>builder()
                                    .message("Logout Success")
                                    .data("OK")
                                    .build();

                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");

                            // Gunakan ObjectMapper untuk menulis object ke dalam body response
                            // ObjectMapper adalah alat standar Spring untuk konversi Java <-> JSON
                            new com.fasterxml.jackson.databind.ObjectMapper().writeValue(response.getWriter(), webResponse);
                        })
                )

                // Tetap aktifkan HTTP Basic agar Swagger bisa 'menembus' lewat tombol Authorize
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                }))

                .sessionManagement(session -> session
                        // Ubah menjadi ALWAYS jika IF_REQUIRED masih belum memancing Cookie keluar
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .securityContext(context -> context
                        .requireExplicitSave(false) // Sangat penting agar session otomatis tersimpan
                );

        return http.build();
    }

}
