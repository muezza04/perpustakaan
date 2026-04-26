package com.nuzurwan.perpustakaan.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id; // Kita gunakan String agar lebih fleksibel di URL

    @Column(unique = true, length = 20) // data harus uniq, data boleh kosong karena bersifat universal
    private String isbn;

    @Column(nullable = false) // data tidak boleh kosong or null
    private String title;

    @Column(nullable = false, length = 150)
    private String author;

    @Column(length = 100)
    private String publisher;

    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Category category;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Loan> loans;
}
