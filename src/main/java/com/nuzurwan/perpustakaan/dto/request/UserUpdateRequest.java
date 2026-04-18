package com.nuzurwan.perpustakaan.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // karena data di request bersifat Mutabilitas
@AllArgsConstructor// Menjamin Jackson/Spring Boot selalu bisa membuat objek dari JSON (Merubah data JSON menjadi object java).
@NoArgsConstructor // Memudahkan membuat Unit Testing tanpa harus memanggil .set() satu per satu. ex : new LoginRequest("nuzurwan", "perpustakaan", "Member").
public class UserUpdateRequest {

    @NotBlank(message = "email wajib di isi")
    @Email(message = "format email tidak valid")
    private String email;

    @NotBlank(message = "wajib isi nama lengkap")
    private String fullName;

    // Role dan isActive dibuat opsional di DTO
    @Schema(description = "perubahan role hanya dilakukan oleh admin")
    private String role;

    @Schema(description = "perubahan isActive hanya dilakukan oleh admin")
    private Boolean isActive; // Boolean nilai(true, false, null) = Wrapper Class atau "pembungkus" untuk tipe data primitif boolean(true, false)
}
