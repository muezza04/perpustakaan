package com.nuzurwan.perpustakaan.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // karena data di request bersifat Mutabilitas
@AllArgsConstructor // Menjamin Jackson/Spring Boot selalu bisa membuat objek dari JSON (Merubah data JSON menjadi object java).
@NoArgsConstructor // Memudahkan membuat Unit Testing tanpa harus memanggil .set() satu per satu.
public class UserCreateRequest {

    @NotBlank(message = "email wajib di isi")
    @Email(message = "format email tidak valid")
    private String email;

    @NotBlank(message = "password wajib di isi")
    @Size(min = 8, message = "password minimal 8 karakter")
    private String password;

    // perubahan dalam data entity, dto(res, req), service = remove fullName change use firstName & lastName
    @NotBlank(message = "wajib isi nama lengkap")
    private String fullName;

}
