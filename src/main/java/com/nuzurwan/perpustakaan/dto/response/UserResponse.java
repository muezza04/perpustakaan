package com.nuzurwan.perpustakaan.dto.response;

import com.nuzurwan.perpustakaan.model.UserRole;
import com.nuzurwan.perpustakaan.model.UserStatus;
import lombok.*;


// Tidak menggunakan Anotasi @Data seperti di request karena di response lebih mengutamakan keamanan
@Getter // Cukup Getter agar Spring/Jackson bisa membaca data untuk diubah jadi JSON
@Builder // Untuk mempermudah mapping dari Entity ke Response di layer Service
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Menjamin kompatibilitas library (Jackson/Hibernate) tanpa mengorbankan Enkapsulasi data.
@AllArgsConstructor // Dibutuhkan agar @Builder bisa bekerja
public class UserResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private UserRole role;
    private UserStatus status;
}
