package com.nuzurwan.perpustakaan.model;

public enum Category {
    // --- RUMPUN ILMU (SUBJECTS) ---

    TECHNOLOGY,       // IT, Teknik, Informatika, Sistem Informasi
    SCIENCE,          // Matematika, Fisika, Kimia, Biologi
    HEALTH_MEDICINE,  // Kedokteran, Keperawatan, Farmasi
    ECONOMY_BUSINESS, // Akuntansi, Manajemen, Kewirausahaan
    SOCIAL_POLITICS,  // Hukum, Hubungan Internasional, Komunikasi
    EDUCATION,        // Keguruan, Psikologi Anak, Kurikulum
    ARTS_DESIGN,      // Desain Grafis, Arsitektur, Musik, Seni Rupa
    RELIGION,         // Studi Islam, Teologi, Filsafat Agama
    HISTORY_GEOGRAPHY,// Sejarah, Budaya, Arkeologi, Pemetaan
    LITERATURE,       // Novel, Karya Sastra, Puisi, Prosa
    LANGUAGE,         // Tata Bahasa, Linguistik (Penting untuk Lab Bahasa)

    // --- FORMAT & KOLEKSI KHUSUS (COLLECTION TYPES) ---

    REFERENCE,        // Kamus, Ensiklopedia, Atlas, Buku Saku (Non-Pinjam)
    JOURNAL,          // Publikasi Ilmiah bulanan/tahunan
    THESIS,           // Skripsi, Tesis, Disertasi (Koleksi Internal)
    PROCEEDINGS,      // Kumpulan hasil seminar/konferensi ilmiah

    // --- LAINNYA ---

    GENERAL           // Buku umum, motivasi, hobi, atau ensiklopedia umum
}