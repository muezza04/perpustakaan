package com.nuzurwan.perpustakaan.controller;

import com.nuzurwan.perpustakaan.dto.request.*;
import com.nuzurwan.perpustakaan.dto.response.PagingResponse;
import com.nuzurwan.perpustakaan.dto.response.UserResponse;
import com.nuzurwan.perpustakaan.dto.response.UserRoleStatusResponse;
import com.nuzurwan.perpustakaan.dto.response.WebResponse;
import com.nuzurwan.perpustakaan.model.UserRole;
import com.nuzurwan.perpustakaan.model.UserStatus;
import com.nuzurwan.perpustakaan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // create guest, member, admin
    @PostMapping(
            // MediaType konstanta (variabel tetap) yang disediakan oleh Spring untuk menghindari kesalahan pengetikan (typo).
            consumes = MediaType.APPLICATION_JSON_VALUE, // Hanya terima JSON
            produces = MediaType.APPLICATION_JSON_VALUE  // Pasti kirim JSON
    )
    @Operation(summary = "Create new User")
    public ResponseEntity<WebResponse<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                WebResponse.<UserResponse>builder()
                        .message("User created successfully")
                        .data(response)
                        .build()
        );
    }

    // read all field user for admin
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Read All User")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<List<UserResponse>>> getAll(
            @ParameterObject @PageableDefault(sort = "firstName") Pageable pageable) {

        // Panggil service dengan parameter pageable
        Page<UserResponse> pageResult = userService.getAllUsers(pageable);

        // Tentukan pesan berdasarkan state isEmpty()
        String message = pageResult.isEmpty() ? "Data not found" : "Successfully fetched all users";

        return ResponseEntity.ok(
                WebResponse.<List<UserResponse>>builder()
                        .message(message)
                        .data(pageResult.getContent()) // Ambil List datanya saja
                        .paging(PagingResponse.builder() // Bungkus metadata pagination
                                .currentPage(pageResult.getNumber())
                                .totalPages(pageResult.getTotalPages())
                                .totalElements(pageResult.getTotalElements())
                                .size(pageResult.getSize())
                                .build())
                        .build()
        );
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Read by Id User")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<UserResponse>> getById(@PathVariable String id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(
                WebResponse.<UserResponse>builder()
                        .message("User found")
                        .data(response)
                        .build()
        );
    }

    // update partiala all user for admin
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update by Id User Opsional")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<UserResponse>> update(@PathVariable String id,@Valid @RequestBody UserUpdateProfileRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(
                WebResponse.<UserResponse>builder()
                        .message("User updated successfully")
                        .data(response)
                        .build()
        );
    }

    // Hard Delete
    @DeleteMapping(value = "/{id}/permanent", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete by Id User permanent")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                WebResponse.<String>builder()
                        .message("User permanently deleted")
                        .data("OK")
                        .build()
        );
    }

    // Soft Delete
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Deactivate delete user")
    public ResponseEntity<WebResponse<UserRoleStatusResponse>> deactivate(@PathVariable String id) {
        UserRoleStatusResponse response = userService.deleteSoftUser(id);
        return ResponseEntity.ok(
                WebResponse.<UserRoleStatusResponse>builder()
                        .message("User deactivated successfully")
                        .data(response)
                        .paging(null)
                        .build()
        );
    }

    // Update partial email
    @PatchMapping("/me/email")
    @PreAuthorize("hasRole('MEMBER')") // bisa menggunakan hirarki authority
    @Operation(
            summary = "Update email user",
            description = "Mengubah email user yang sedang login. Memerlukan verifikasi password untuk alasan keamanan."
    )
    public ResponseEntity<WebResponse<UserResponse>> updateEmail(
            @Valid @RequestBody UserUpdateEmailRequest request) {

        // Kita tidak mengirimkan 'id' di sini karena Service akan mengambilnya
        // langsung dari SecurityContextHolder (Implicit Identity).
        UserResponse response = userService.updateEmail(request);

        return ResponseEntity.ok(
                WebResponse.<UserResponse>builder()
                        .message("Email berhasil diperbarui. Silakan gunakan email baru pada login berikutnya.")
                        .data(response)
                        .build()
        );
    }

    // update partial role-status
    @PatchMapping("/{userId}/role-status")
    @PreAuthorize("hasRole('ADMIN')") // <--- Kunci Utama: Hanya user dengan role ADMIN yang bisa lewat
    @Operation(
            summary = "Update Role dan Status User",
            description = "Endpoint administratif untuk mengubah hak akses (Role) dan status aktif user berdasarkan ID."
    )
    public ResponseEntity<WebResponse<UserResponse>> updateRoleStatus(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateRoleStatusRequest request) {

        UserResponse response = userService.updateRoleStatus(userId, request);

        return ResponseEntity.ok(
                WebResponse.<UserResponse>builder()
                        .message("Berhasil memperbarui hak akses dan status user")
                        .data(response)
                        .build()
        );
    }

    // update partial password
    @PatchMapping("/me/password")
    @Operation(
            summary = "Update password user",
            description = "Mengubah password user yang sedang aktif. Memerlukan verifikasi password lama dan konfirmasi password baru." +
                    "\n Jika password berhasil di perbaharui cookie akan di reset, silahkan login kembali dengan password terbaru"
    )
    public ResponseEntity<WebResponse<String>> updatePassword(
            @Valid @RequestBody UserUpdatePasswordRequest request) {

        String resultMessage = userService.updatePassword(request);

        return ResponseEntity.ok(
                WebResponse.<String>builder()
                        .message(resultMessage)
                        .data("OK")
                        .build()
        );
    }

    // search for only admin
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search & Filter Users", description = "Mencari user berdasarkan nama/email dan filter kategori.")
    public ResponseEntity<WebResponse<List<UserResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status,
            /// menggunakan anotasi @PrameterObject untuk membuat pageable tidak required di swagger
            @ParameterObject @PageableDefault(sort = "firstName") Pageable pageable) {

        Page<UserResponse> pageResult = userService.search(keyword, role, status, pageable);

        // Best Practice: Berikan pesan yang membantu User
        String message = pageResult.isEmpty() ? "Data not found" : "Successfully retrieved user data";

        return ResponseEntity.ok(
                WebResponse.<List<UserResponse>>builder()
                        .message(message)
                        .data(pageResult.getContent()) // Hanya ambil kontennya saja (List)
                        .paging(PagingResponse.builder() // Rakit metadata halamannya sendiri
                                .currentPage(pageResult.getNumber())
                                .totalPages(pageResult.getTotalPages())
                                .totalElements(pageResult.getTotalElements())
                                .size(pageResult.getSize())
                                .build())
                        .build()
        );
    }

    // update partial profile session
    @PatchMapping(
            value = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    @Operation(
            summary = "Update My Profile",
            description = "Memperbarui data nama depan dan nama belakang pengguna yang sedang login."
    )
    public ResponseEntity<WebResponse<UserResponse>> updateMyProfile(
            @RequestBody @Valid UserUpdateProfileRequest request) {

        // Panggil service tanpa mengirimkan ID dari luar
        UserResponse response = userService.updateMyProfile(request);

        return ResponseEntity.ok(
                WebResponse.<UserResponse>builder()
                        .message("Profil berhasil diperbarui")
                        .data(response)
                        .build()
        );
    }

    // read only session
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    @Operation(summary = "Get My Profile", description = "Mengambil data profil lengkap milik user yang sedang login.")
    public ResponseEntity<WebResponse<UserResponse>> getMyProfile() {
        UserResponse response = userService.getMyProfile();
        return ResponseEntity.ok(
                WebResponse.<UserResponse>builder()
                        .message("Profil berhasil dimuat")
                        .data(response)
                        .build()
        );
    }
}
