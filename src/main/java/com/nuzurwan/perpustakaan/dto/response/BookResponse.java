package com.nuzurwan.perpustakaan.dto.response;

import com.nuzurwan.perpustakaan.model.Category;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer releaseYear;
    private int stock;
    private Category category;
}
