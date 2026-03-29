package com.nuzurwan.perpustakaan.dto.request;

import com.nuzurwan.perpustakaan.model.Category;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateBookRequest {

    @Size(min = 10, max = 13, message = "ISBN harus antara 10 sampai 13 karakter")
    private String isbn;

    @NotBlank(message = "Judul buku wajib diisi")
    @Size(max = 255, message = "Nama Penulis terlalu panjang (max: 255 karakter)")
    private String title;

    @NotBlank(message = "Nama penulis wajib diisi")
    @Size(max = 255, message = "Nama Penulis terlalu panjang (max: 255 karakter)")
    private String author;

    @Size(max = 255, message = "Nama penerbit terlalu panjang (max: 255 karakter)")
    private String publisher;

    @NotNull(message = "Tahun terbit wajib diisi")
    @Min(value = 1900, message = "Tahun terbit tidak valid")
    // Gunakan logika dinamis di Service untuk Max tahun saat ini
    private Integer releaseYear;

    @Min(value = 0, message = "Stok tidak boleh negatif")
    @Max(value = 999, message = "Stok maksimal 999 per judul buku")
    private int stock;

    @NotNull(message = "Kategori buku wajib dipilih")
    private Category category;
}