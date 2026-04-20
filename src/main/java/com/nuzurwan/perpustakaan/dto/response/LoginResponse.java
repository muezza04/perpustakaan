package com.nuzurwan.perpustakaan.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Standar Industri: Akses terbatas (Protected) guna menjaga integritas objek agar tetap diinstansiasi melalui Builder.
public class LoginResponse {
    private UserResponse user; // Kita bungkus UserResponse di dalamnya (Composition)
    // Jika nanti pakai JWT, tokennya ditaruh di sini
}
