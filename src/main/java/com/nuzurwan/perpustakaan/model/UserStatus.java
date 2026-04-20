package com.nuzurwan.perpustakaan.model;

public enum UserStatus {
    ACTIVE,      // Bisa login & akses penuh (MEMBER)
    PENDING,     // Bisa login & akses terbatas (GUEST)
    DEACTIVATED  // Tidak bisa login sama sekali (Banned)
}