package com.nuzurwan.perpustakaan.dto.response;

import com.nuzurwan.perpustakaan.model.UserRole;
import lombok.*;

// Tidak menggunakan Anotasi @Data seperti di request karena di response lebih mengutamakan keamanan
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String fullName;
    private UserRole role;
    private boolean isActive;
}
