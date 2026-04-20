package com.nuzurwan.perpustakaan.service;

import com.nuzurwan.perpustakaan.dto.request.UserCreateRequest;
import com.nuzurwan.perpustakaan.dto.request.UserUpdateRequest;
import com.nuzurwan.perpustakaan.dto.response.UserResponse;
import com.nuzurwan.perpustakaan.model.User;
import com.nuzurwan.perpustakaan.model.UserRole;
import com.nuzurwan.perpustakaan.model.UserStatus;
import com.nuzurwan.perpustakaan.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    // Spring mengambil objek dari "gudang" (Container) dan memasukkannya ke sini
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // (Create) Register user
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

    // (Read All)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        if(users.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Users not found");

        return users.stream().map(this::mapToResponse).toList();
    }

    // (Read by ID)
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapToResponse(user);
    }

    // (Update)
    public UserResponse updateUser(String Userid, UserUpdateRequest request) {
        // Cek id menggunakan findById yang disediakan
        User user = userRepository.findById(Userid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Validasi penting untuk update tertentu seperti hanya ADMIN yg bisa update ROLE(sementara) dan isActive
        // isActive bisa diberikan logika ketika tidak active lebih dari 1 tahun akunnya menjadi tidak active

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        // Untuk role dan isActive hanya abmin yg bisa update, untuk role guest bisa melakukan request update role to admin
        user.setRole(UserRole.valueOf(request.getRole()));
        user.setStatus(UserStatus.valueOf(request.getStatus()));

        return  mapToResponse(userRepository.save(user));
    }

    // (Delete Soft)
    public void deleteSoftUser(String id) {
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);
    }

    // (Delete Permanent)
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        userRepository.deleteById(id);
    }

    // (Search)
    // (update password)

    // Halper: Entity -> Response
    private UserResponse mapToResponse(User user) {

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
