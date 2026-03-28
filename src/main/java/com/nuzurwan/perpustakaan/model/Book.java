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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // data harus uniq, data boleh kosong karena bersifat universal
    private String isbn;

    @Column(nullable = false) // data tidak boleh kosong or null
    private String title;

    private String author;
    private String publisher;
    private Integer releaseYear;

    @Column(nullable = false) // data tidak boleh kosong atau null
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
}
