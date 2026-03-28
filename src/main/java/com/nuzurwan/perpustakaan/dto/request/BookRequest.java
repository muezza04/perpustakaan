package com.nuzurwan.perpustakaan.dto.request;

import com.nuzurwan.perpustakaan.model.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequest {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer releaseYear;
    private int stock;
    private Category category;
}
