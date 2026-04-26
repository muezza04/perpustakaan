package com.nuzurwan.perpustakaan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // karena data di request bersifat Mutabilitas
@AllArgsConstructor // Memudahkan membuat Unit Testing tanpa harus memanggil .set() satu per satu. ex : new LoginRequest("nuzurwan", "perpustakaan", "Member").
@NoArgsConstructor // Menjamin Jackson/Spring Boot selalu bisa membuat objek dari JSON (Merubah data JSON menjadi object java).
public class UserUpdateProfileRequest {

    @NotBlank(message = "First Name is required")
    @Size(max = 50, message = "First Name maximal 50 characters")
    private String firstName;

    // Tidak pakai @NotBlank karena lastName bisa saja kosong (opsional)
    @Size(max = 50, message = "Last Name maximal 50 characters")
    private String lastName;
}
