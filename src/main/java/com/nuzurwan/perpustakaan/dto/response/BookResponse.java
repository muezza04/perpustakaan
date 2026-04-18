package com.nuzurwan.perpustakaan.dto.response;

import com.nuzurwan.perpustakaan.model.Category;
import lombok.*;

// Tidak menggunakan Anotasi @Data seperti di request karena di response lebih mengutamakan keamanan
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private String id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer releaseYear;
    private int stock;
    private Category category;
}
