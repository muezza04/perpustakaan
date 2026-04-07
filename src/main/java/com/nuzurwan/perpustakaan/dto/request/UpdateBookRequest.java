package com.nuzurwan.perpustakaan.dto.request;

import com.nuzurwan.perpustakaan.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateBookRequest {

    // ISBN tetap menggunakan Pattern agar jika user ingin memperbaiki salah ketik ISBN,
    // formatnya tetap terjaga. Tapi karena skripsi/buku tua boleh kosong, kita tetap pakai ^$
    @Pattern(regexp = "^$|[0-9\\-xX]{10,17}", message = "Format ISBN tidak valid")
    @Schema(example = "978-602-8519-93-9", description = "Kosongkan jika buku tidak memiliki ISBN")
    private String isbn;

    @NotBlank(message = "Judul buku tidak boleh kosong")
    @Size(max = 255, message = "Judul terlalu panjang")
    private String title;

    @NotBlank(message = "Nama penulis tidak boleh kosong")
    @Size(max = 255, message = "Nama penulis terlalu panjang")
    private String author;

    @Size(max = 255, message = "Nama penerbit terlalu panjang")
    private String publisher;

    @NotNull(message = "Tahun terbit wajib diisi")
    @Min(value = 1900, message = "Tahun terbit minimal 1900")
    private Integer releaseYear;

    @Min(value = 0, message = "Stok tidak boleh negatif")
    @Max(value = 999, message = "Stok maksimal 999")
    private int stock;

    @NotNull(message = "Kategori buku wajib dipilih")
    private Category category;
}