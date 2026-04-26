package com.nuzurwan.perpustakaan.model;

public enum BookStatus {
    AVAILABLE,  // Buku ada di rak dan siap untuk dipinjam.
    BORROWED,   // Buku sedang dipinjam oleh member.
    RESERVED,   // Buku sedang dipesan oleh orang lain (Booking).
    LOST,       // Buku dinyatakan hilang (oleh member atau saat stok opname).
    DAMAGED,    // Buku dalam kondisi rusak dan tidak layak pinjam.
    IN_REPAIR,  // Buku sedang dalam proses perbaikan/penjilidan ulang.
    DISCARDED   // Buku sudah dikeluarkan dari koleksi (dibuang/dihibahkan).
}
