package com.nuzurwan.perpustakaan.dto.request;

import com.nuzurwan.perpustakaan.model.UserRole;
import com.nuzurwan.perpustakaan.model.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // karena data di request bersifat Mutabilitas
@AllArgsConstructor
// Memudahkan membuat Unit Testing tanpa harus memanggil .set() satu per satu. ex : new LoginRequest("nuzurwan", "perpustakaan", "Member").
@NoArgsConstructor
// Menjamin Jackson/Spring Boot selalu bisa membuat objek dari JSON (Merubah data JSON menjadi object java).
public class UserUpdateRoleStatusRequest {
    @NotNull(message = "This field is required")
    @Schema(example = "MEMBER", description = "enum data : [ ADMIN, MEMBER, GUEST ]")
    private UserRole Role;

    @NotNull(message = "This field is required")
    @Schema(example = "ACTIVE", description = "enum data : [ ACTIVE, PENDING, DEACTIVATED ]")
    private UserStatus Status;
}
