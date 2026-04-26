package com.nuzurwan.perpustakaan.service;

import com.nuzurwan.perpustakaan.dto.request.*;
import com.nuzurwan.perpustakaan.dto.response.UserResponse;
import com.nuzurwan.perpustakaan.dto.response.UserRoleStatusResponse;
import com.nuzurwan.perpustakaan.model.User;
import com.nuzurwan.perpustakaan.model.UserRole;
import com.nuzurwan.perpustakaan.model.UserStatus;
import com.nuzurwan.perpustakaan.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    // Spring mengambil objek dari "gudang" (Container) dan memasukkannya ke sini
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /// (Create) Register user
    @Transactional
    public UserResponse createUser(@NonNull UserCreateRequest request) {

        // Ambil dan bersihkan email di awal (lapisan keamaan)
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The email field must not be left blank");
        }

        // rubah huruf email menjadi kecil semua, untuk mengatasi celah sistem, karena secara umum email baik ada huruf besar atau kecil akan tetap dijadikan 1 email
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // Cek Duplikasi menggunakan email yang sudah bersih
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address already registered");
        }

        // Name Handling (Sanitization)
        // Kita trim agar tidak ada spasi sisa di awal/akhir nama
        String cleanFirstName = request.getFirstName().trim();

        // Gunakan ternary untuk lastName agar jika null tidak menyebabkan masalah
        String cleanLastName = (request.getLastName() != null && !request.getLastName().isBlank()) ? request.getLastName().trim() : null;

        // Request -> Entity (Gunakan normalizedEmail agar konsisten di DB)
        User user = User.builder()
                .email(normalizedEmail)
                .firstName(cleanFirstName)
                .lastName(cleanLastName)
                .password(passwordEncoder.encode(request.getPassword())) // encode password
                .build();

        // Simpan dan Map ke Response
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    /// (Read All)
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        // Gunakan findAll(pageable) untuk limitasi data di level database
        Page<User> users = userRepository.findAll(pageable);

        // Stream pada Page otomatis menjaga struktur pagination
        return users.map(this::mapToResponse);
    }

    /// (Read by ID)
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapToResponse(user);
    }

    /// (Read all Session)
    @Transactional(readOnly = true)
    public UserResponse getMyProfile() {
        // Ambil autentikasi dari Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Anda belum login");
        }

        String currentEmail = authentication.getName();

        // Cari di database
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil tidak ditemukan"));

        // Kembalikan UserResponse penuh (id, email, nama, role, status)
        return mapToResponse(user);
    }

    /// (Update Profile)
    @Transactional
    public UserResponse updateUser(String Userid, @NonNull UserUpdateProfileRequest request) {
        // Cek id menggunakan findById yang disediakan
        User user = userRepository.findById(Userid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Name Handling (Sanitization)
        // Kita trim agar tidak ada spasi sisa di awal/akhir nama
        String cleanFirstName = request.getFirstName().trim();

        // Gunakan ternary untuk lastName agar jika null tidak menyebabkan masalah
        String cleanLastName = (request.getLastName() != null && !request.getLastName().isBlank()) ? request.getLastName().trim() : null;

        user.setFirstName(cleanFirstName);
        user.setLastName(cleanLastName);

        return  mapToResponse(userRepository.save(user));
    }

    /// (Update me session)
    @Transactional
    public UserResponse updateMyProfile(@NonNull UserUpdateProfileRequest request) {
        // Ambil informasi autentikasi dari Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Defensive check untuk memastikan session valid
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesi tidak valid atau telah berakhir");
        }

        // Ambil email/username dari principal
        String currentEmail = authentication.getName();

        // Cari user berdasarkan email dari session (bukan dari input user)
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil pengguna tidak ditemukan"));

        // Sanitasi data (Trim spasi)
        String cleanFirstName = request.getFirstName().trim();
        String cleanLastName = (request.getLastName() != null && !request.getLastName().isBlank())
                ? request.getLastName().trim() : null;

        // Update field yang diizinkan (Hanya nama, jangan email/role di sini)
        user.setFirstName(cleanFirstName);
        user.setLastName(cleanLastName);

        // Simpan ke MySQL dan kembalikan DTO
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    /// (Update me Email)
    @Transactional
    public UserResponse updateEmail(UserUpdateEmailRequest request) {
        // Ambil identitas dari SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Gunakan pengecekan singkat atau percayakan pada SecurityConfig
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session invalid");
        }

        String currentEmail = authentication.getName();

        // Cari user dengan cara yang aman (Anti NoSuchElementException)
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User data not found"));

        // Verifikasi Password (Challenge)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Verification failed: Invalid password");
        }

        String normalizedEmail = request.getNewEmail().trim().toLowerCase();

        // Cek redundansi Email Baru
        // Best Practice: Pastikan email baru berbeda dengan email lama sebelum cek ke DB
        if (user.getEmail().equalsIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New email cannot be the same as current email");
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use by another account");
        }

        // 5. Update & Save
        user.setEmail(normalizedEmail);
        userRepository.save(user);

        return mapToResponse(user);
    }

    /// (Update Role Status)
    @Transactional
    public UserResponse updateRoleStatus(String userId, @NonNull UserUpdateRoleStatusRequest request) {
        // 1. Cari user yang akan diubah (Target User)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ///  Validasi untuk update menggunakan enum belum dilakukan
        // 2. Update data dari Request
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());

        // 3. Simpan perubahan
        userRepository.save(user);

        // 4. Return menggunakan helper mapping agar rapi
        return mapToResponse(user);
    }

    /// (Update me) Change Password
    @Transactional
    public String updatePassword(UserUpdatePasswordRequest request) {
        // Ambil email user yang sedang login dari Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Gunakan pengecekan singkat atau percayakan pada SecurityConfig
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session invalid");
        }

        String currentEmail = authentication.getName();

        // Cari user di database
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));

        // VALIDASI A: Cek apakah Password Lama cocok dengan yang ada di DB
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password lama tidak sesuai");
        }

        // VALIDASI B: Cek apakah Password Baru sama dengan Konfirmasi Password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Konfirmasi password baru tidak cocok");
        }

        // VALIDASI C: Pastikan password baru tidak sama dengan password lama (Opsional tapi Best Practice)
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password baru tidak boleh sama dengan password lama");
        }

        // Update Password (WAJIB DI-ENCODE!)
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // ketika sudah ganti password disarankan untuk login kembali untuk menghindari session Hijacking
        SecurityContextHolder.clearContext();

        return "Password berhasil diperbarui, silahkan login kembali";
    }

    /// (Search only admin)
    @Transactional(readOnly = true)
    public Page<UserResponse> search(String keyword, UserRole role, UserStatus status, Pageable pageable) {
        // Specification memungkinkan kita membuat query dinamis secara programatik
        Specification<User> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Logika Keyword (Search di firstName, lastName, dan email)
            if (keyword != null && !keyword.isBlank()) {
                String lowerKeyword = "%" + keyword.toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), lowerKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), lowerKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), lowerKeyword)
                );
                predicates.add(keywordPredicate);
            }

            // 2. Filter berdasarkan Role (Jika ada)
            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            // 3. Filter berdasarkan Status (Jika ada)
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> users = userRepository.findAll(specification, pageable);

        // Transformasi Page<User> menjadi Page<UserResponse>
        return users.map(this::mapToResponse);
    }

    // (Delete Soft)
    @Transactional
    public UserRoleStatusResponse deleteSoftUser(String id) {
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        user.setStatus(UserStatus.DEACTIVATED);

        userRepository.save(user);

        return UserRoleStatusResponse.builder().id(user.getId()).status(user.getStatus()).build();
    }

    // (Delete Permanent)
    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        userRepository.deleteById(id);
    }

    // Halper: Entity -> Response
    private UserResponse mapToResponse(@NonNull User user) {

        // Logic Ternary operator
        // Logika: Jika lastName tidak null DAN tidak kosong, maka gabungkan. Jika tidak, ambil firstName saja.
        String fullName = (user.getLastName() != null && !user.getLastName().isBlank())
                ? user.getFirstName() + " " + user.getLastName()
                : user.getFirstName();

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName() == null ? "" : user.getLastName())
                .fullName(fullName)
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
