package com.nuzurwan.perpustakaan.service;

import com.nuzurwan.perpustakaan.dto.request.BookCreateRequest;
import com.nuzurwan.perpustakaan.dto.request.BookUpdateRequest;
import com.nuzurwan.perpustakaan.dto.response.BookResponse;
import com.nuzurwan.perpustakaan.model.Book;
import com.nuzurwan.perpustakaan.model.Category;
import com.nuzurwan.perpustakaan.repository.BookRepository;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull; // untuk memastikan data yang diterima(request) tidak null
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// respon status error
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Otomatis menghubungkan Repository (Dependency Injection)
public class BookService {

    private final BookRepository bookRepository;

    // (Add) data
    public BookResponse createBook(@NonNull BookCreateRequest request) {
        // --- 1. VALIDASI LOGIKA BISNIS ---

        // A. Ambil nilai ISBN dari request
        String finalIsbn = request.getIsbn();

        if (finalIsbn != null && !finalIsbn.isBlank()) {
            // 1. VALIDASI: Jika TIDAK ada strip, maka MAX 13 karakter
            if (!finalIsbn.contains("-") && finalIsbn.length() > 13) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "ISBN tanpa tanda hubung (strip) maksimal 13 karakter!");
            }

            /* 1. VALIDASI WAJIB STRIP: Cek apakah teks mengandung tanda hubung
            if (!finalIsbn.contains("-")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "ISBN wajib menggunakan tanda hubung (strip). Contoh: 978-602-8519-93-9");
            } */

            // 2. Ubah SEMUA menjadi HURUF BESAR (x jadi X) Hapus strip (-)
            finalIsbn = finalIsbn.replace("-", "").toUpperCase(); // <--- TAMBAHKAN .toUpperCase()

            // Cek Duplikasi setelah dibersihkan dan di-Besarkan
            if (bookRepository.existsByIsbn(finalIsbn)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN sudah terdaftar!");
            }
        } else {
            // Buat internal ID jika tidak memiliki ISBN
            // substring(0, 8) berfungsi untuk memotong UUID yang panjang 36 menjadi 8
            finalIsbn = "INTERNAL-" + UUID.randomUUID().toString().substring(0, 8);
        }

        // B. Validasi Tahun Terbit Dinamis (Tidak boleh melebihi tahun saat ini)
        int currentYear = LocalDate.now().getYear();
        if (request.getReleaseYear() != null && request.getReleaseYear() > currentYear) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tahun terbit tidak boleh melebihi tahun saat ini (" + currentYear + ")");
        }

        // 1. Validasi Manual untuk Enum
        if (!Category.isValid(request.getCategory())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori '" + request.getCategory() + "' tidak tersedia!");
        }

        // --- 2. PROSES MAPPING & SAVE ---

        // Manual Request to Entity bersifat khusus
        Book book = Book.builder()
                .isbn(finalIsbn) // Gunakan ISBN yang sudah bersih/generated
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .releaseYear(request.getReleaseYear())
                .stock(request.getStock())
                .category(Category.valueOf(request.getCategory())) // tipe data string request menjadi category
                .build();

        // Simpan ke database (Hasil simpan ditampung di variabel 'savedBook')
        Book savedBook = bookRepository.save(book);

        // Kembalikan dalam bentuk Entity -> Response DTO (Lengkap dengan ID baru)
        return mapToResponse(savedBook);
    }

    // (Read) All data
    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        // VALIDASION: Cek apakah list kosong
        if (books.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Daftar buku masih kosong!");
        }

        // Mengubah List<Book> menjadi List<BookResponse>
        return books.stream().map(this::mapToResponse).toList();
    }

    // (Read) by id data
    public BookResponse getBookById(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku dengan ID " + id + " tidak ditemukan!"));
        return mapToResponse(book);
    }

    // (Update) by id data
    public BookResponse updateBook(String id, @NonNull BookUpdateRequest request) {
        // 1. Cari data lama
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan!"));

        // 2. Normalisasi ISBN (Jika user mengubah ISBN)
        String finalIsbn = request.getIsbn();

        if (finalIsbn != null && !finalIsbn.isBlank()) {
            // 1. VALIDASI: Jika TIDAK ada strip, maka MAX 13 karakter
            if (!finalIsbn.contains("-") && finalIsbn.length() > 13) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "ISBN tanpa tanda hubung (-) maksimal 13 karakter!");
            }

            /* 1. VALIDASI WAJIB STRIP: Cek apakah teks mengandung tanda hubung
            if (!finalIsbn.contains("-")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "ISBN wajib menggunakan tanda hubung (strip). Contoh: 978-602-8519-93-9");
            } */

            // 2. Normalisasi: Baru hapus strip dan ubah ke huruf besar setelah divalidasi
            String cleanIsbn = finalIsbn.replace("-", "").toUpperCase();

            // 3. Cek duplikasi: Bandingkan dengan ISBN yang sudah bersih di DB
            if (!cleanIsbn.equals(book.getIsbn()) && bookRepository.existsByIsbn(cleanIsbn)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN sudah digunakan buku lain!");
            }

            book.setIsbn(cleanIsbn);
        }

        int currentYear = LocalDate.now().getYear();
        if (request.getReleaseYear() > currentYear) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tahun terbit tidak boleh melebihi tahun saat ini (" + currentYear + ")");
        }

        // Validasi Manual untuk Enum
        if (!Category.isValid(request.getCategory())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori '" + request.getCategory() + "' tidak tersedia!");
        }

        // 3. Update field lainnya
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setReleaseYear(request.getReleaseYear());
        book.setStock(request.getStock());
        book.setCategory(Category.valueOf(request.getCategory())); // wajib memiliki validari manual enum sebelum string dirubah ke categry

        return mapToResponse(bookRepository.save(book));
    }

    // (Delete) by id Book
    public void deleteBook(String id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found!");
        }
        bookRepository.deleteById(id);
    }

    // (Search) by isbn, title & author
    public List<BookResponse> searchBooks(@NotNull String keyword) {
        // 1. VALIDASI INPUT: Cegah keyword kosong atau null, trim() = untuk menghapus spasi berlebih di awal dan akhir kata
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kata kunci pencarian tidak boleh kosong!");
        }

        // 2. PROSES PENCARIAN
        List<Book> books = bookRepository.searchBooks(keyword);

        // 3. VALIDASI HASIL: Jika tidak ada buku yang cocok
        if (books.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Buku dengan kata kunci '" + keyword + "' tidak ditemukan");
        }

        // 4. MAPPING KE RESPONSE
        return books.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Helper: Entity -> Response (result)
    private BookResponse mapToResponse(@NonNull Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .releaseYear(book.getReleaseYear())
                .stock(book.getStock())
                .category(book.getCategory())
                .build();
    }
}