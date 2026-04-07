package com.nuzurwan.perpustakaan.dto.request;

import com.nuzurwan.perpustakaan.model.Category;
import io.swagger.v3.oas.annotations.media.Schema; // memberikan meta-data atau informasi tambahan pada sebuah class atau field
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateBookRequest {

    // Alasan penggunaan pattern karena bisa mengatur pola validasi tanpa ada celah
    // " ^$ " = string kosong, " | " = logika OR, " [0-9\-xX] " = data hanya angka, tanda hubung strip, serta huruf 'x' baik kecil maupun kapital untuk mendukung format ISBN lama
    // " {10,17} " = membatasi panjang input minimal 10 karakter dan maksimal 17 karakter untuk mengakomodasi standar ISBN-10 dan ISBN-13 beserta pemisahnya(tanda strip)
    @Pattern(regexp = "^$|[0-9\\-xX]{10,17}", message = "ISBN tidak valid (Gunakan 10-13 angka ex: , karakter 'X' dan '-' diperbolehkan)")
    @Schema(example = "978-602-8519-93-9", description = "ISBN 13 digit dengan tanda hubung(-)")
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