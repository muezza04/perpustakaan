package com.nuzurwan.perpustakaan.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateEmailRequest {

    @NotBlank(message = "New email address is required")
    @Email(message = "Invalid email format")
    private String newEmail;

    @NotBlank(message = "Current password is required for verification")
    private String password; // Standar Industri: Wajib verifikasi password

}
