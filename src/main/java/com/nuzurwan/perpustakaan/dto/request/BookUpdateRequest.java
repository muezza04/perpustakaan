package com.nuzurwan.perpustakaan.dto.request;

import com.nuzurwan.perpustakaan.model.BookStatus;
import com.nuzurwan.perpustakaan.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // karena data di request bersifat Mutabilitas
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {

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

    @NotNull(message = "This field is required")
    private BookStatus status;

    @NotNull(message = "This field is required")
    @Schema(example = "TECHNOLOGY", description = "[ TECHNOLOGY, SCIENCE, HEALTH_MEDICINE, ECONOMY_BUSINESS, SOCIAL_POLITICS, EDUCATION, ARTS_DESIGN, RELIGION, HISTORY_GEOGRAPHY, LITERATURE, LANGUAGE, REFERENCE, JOURNAL, THESIS, PROCEEDINGS, GENERAL ]")
    private Category category;
}