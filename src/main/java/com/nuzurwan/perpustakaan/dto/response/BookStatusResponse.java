package com.nuzurwan.perpustakaan.dto.response;

import com.nuzurwan.perpustakaan.model.BookStatus;
import lombok.*;

@Getter // Cukup Getter agar Spring/Jackson bisa membaca data untuk diubah jadi JSON
@Builder // Untuk mempermudah mapping dari Entity ke Response di layer Service
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Menjamin kompatibilitas library (Jackson/Hibernate) tanpa mengorbankan Enkapsulasi data.
@AllArgsConstructor // Dibutuhkan agar @Builder bisa bekerja
public class BookStatusResponse {
    private String id;
    private String title;
    private BookStatus status;
}
