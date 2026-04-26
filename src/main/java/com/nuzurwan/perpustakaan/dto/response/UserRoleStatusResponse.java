package com.nuzurwan.perpustakaan.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nuzurwan.perpustakaan.model.UserRole;
import com.nuzurwan.perpustakaan.model.UserStatus;
import lombok.*;

@Getter // Cukup Getter agar Spring/Jackson bisa membaca data untuk diubah jadi JSON
@Builder // Untuk mempermudah mapping dari Entity ke Response di layer Service
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Menjamin kompatibilitas library (Jackson/Hibernate) tanpa mengorbankan Enkapsulasi data.
@AllArgsConstructor // Dibutuhkan agar @Builder bisa bekerja
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRoleStatusResponse {
    private String id;
    private UserRole Role;
    private UserStatus status;
}
