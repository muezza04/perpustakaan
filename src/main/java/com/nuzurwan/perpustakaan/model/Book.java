package com.nuzurwan.perpustakaan.model;

import jakarta.persistence.*;
import lombok.*;

@Entity // Memberitahu Spring Boot bahwa ini adalah tabel database
@Table(name = "books") // Nama tabel di MySQL nanti
@Getter // Otomatis membuatkan semua Getter
@Setter // Otomatis membuatkan semua Setter
@NoArgsConstructor // Membuat constructor kosong (Wajib untuk JPA)
@AllArgsConstructor // Membuat constructor dengan semua parameter
@Builder // Memudahkan Anda membuat objek dengan gaya Book.builder().title("Java").build()
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Spring Boot 3 / Hibernate 6 style
    @Column(name = "id", updatable = false, nullable = false)
    private String id; // Kita gunakan String agar lebih fleksibel di URL

    @Column(unique = true) // data harus uniq, data boleh kosong karena bersifat universal
    private String isbn;

    @Column(nullable = false) // data tidak boleh kosong or null
    private String title;

    @Column(nullable = false)
    private String author;

    private String publisher;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
}
