package com.nuzurwan.perpustakaan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // karena data di request bersifat Mutabilitas
@AllArgsConstructor // Menjamin Jackson/Spring Boot selalu bisa membuat objek dari JSON (Merubah data JSON menjadi object java).
@NoArgsConstructor // Memudahkan membuat Unit Testing tanpa harus memanggil .set() satu per satu.
public class UserChangePasswordRequest {

    @NotBlank(message = "Password lama wajib diisi")
    private String oldPassword;

    @NotBlank(message = "Password baru wajib diisi")
    @Size(min = 8, message = "Password baru minimal 8 karakter")
    private String newPassword;

    @NotBlank(message = "Konfirmasi password baru wajib diisi")
    private String confirmPassword;
}
