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

    @NotBlank(message = "Email address is required")
    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "First Name is required")
    @Size(max = 50, message = "First Name maximal 50 characters")
    private String firstName;

    // Tidak pakai @NotBlank karena lastName bisa saja kosong (opsional)
    @Size(max = 50, message = "Last Name maximal 50 characters")
    private String lastName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password minimal 8 dan maximal 100 characters")
    private String password;
}
