package com.nuzurwan.perpustakaan.service;

import com.nuzurwan.perpustakaan.dto.request.UserCreateRequest;
import com.nuzurwan.perpustakaan.dto.request.UserUpdateRequest;
import com.nuzurwan.perpustakaan.dto.response.UserResponse;
import com.nuzurwan.perpustakaan.model.User;
import com.nuzurwan.perpustakaan.repository.UserRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRespository userRespository;

    // (Create) Register user
    public UserResponse createUser(UserCreateRequest request) {

        // request -> entity
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .build();

        // entity -> response
        return  mapToResponse(userRespository.save(user));
    }

    // (Read All) User
    public List<UserResponse> getAllUsers() {
        List<User> users = userRespository.findAll();

        return users.stream().map(this::mapToResponse).toList();
    }

    // (Read by ID)
    public UserResponse getUserById(String id) {
        User user = userRespository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapToResponse(user);
    }

    // (Update)
    public UserResponse updateUser(String Userid, UserUpdateRequest request) {
        // Cek id menggunakan findById yang disediakan
        User user = userRespository.findById(Userid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Validasi penting untuk update tertentu seperti hanya ADMIN yg bisa update ROLE(sementara) dan isActive
        // isActive bisa diberikan logika ketika tidak active lebih dari 1 tahun akunnya menjadi tidak active

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        return  mapToResponse(userRespository.save(user));
    }

    // (Delete Soft) ??? check validation replay
    public void deleteSoftUser(String id) {
        User user =  userRespository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        user.setIsActive(false);
        userRespository.save(user);
    }

    // (Delete Permanent)
    public void deleteUser(String id) {
        if (!userRespository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        userRespository.deleteById(id);
    }

    // (Login) // what the void? auth? need response same?
    // (Search)
    // (update password)

    // Halper: Entity -> Response
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }
}
