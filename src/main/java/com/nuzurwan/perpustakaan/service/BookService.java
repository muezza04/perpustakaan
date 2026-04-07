package com.nuzurwan.perpustakaan.service;

import com.nuzurwan.perpustakaan.dto.request.BookRequest;
import com.nuzurwan.perpustakaan.dto.request.CreateBookRequest;
import com.nuzurwan.perpustakaan.dto.response.BookResponse;
import com.nuzurwan.perpustakaan.model.Book;
import com.nuzurwan.perpustakaan.repository.BookRepository;
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
    public BookResponse createBook(CreateBookRequest request) {
        // --- 1. VALIDASI LOGIKA BISNIS ---

        // A. Ambil nilai ISBN dari request
        String finalIsbn = request.getIsbn();

        if (finalIsbn != null && !finalIsbn.isBlank()) {
            // 1. Hapus strip (-)
            // 2. Ubah SEMUA menjadi HURUF BESAR (x jadi X)
            finalIsbn = finalIsbn.replace("-", "").toUpperCase(); // <--- TAMBAHKAN .toUpperCase()

            // Cek Duplikasi setelah dibersihkan dan di-Besarkan
            if (bookRepository.existsByIsbn(finalIsbn)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN sudah terdaftar!");
            }
        } else {
            // Buat internal ID jika tidak memiliki ISBN
            finalIsbn = "INTERNAL-" + UUID.randomUUID().toString().substring(0, 8);
        }

        // B. Validasi Tahun Terbit Dinamis (Tidak boleh melebihi tahun saat ini)
        int currentYear = LocalDate.now().getYear();
        if (request.getReleaseYear() != null && request.getReleaseYear() > currentYear) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tahun terbit tidak boleh melebihi tahun saat ini (" + currentYear + ")");
        }

//        // CARI KATEGORI VALITADION
//        // Jika Category adalah Enum, Spring akan otomatis error 400 jika input user tidak sesuai nama Enum.
//        if (request.getCategory() == null || request.getCategory() != ) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori wajib dipilih!");
//        }

        // --- 2. PROSES MAPPING & SAVE ---

        // Manual Request to Entity bersifat khusus
        Book book = Book.builder()
                .isbn(finalIsbn) // Gunakan ISBN yang sudah bersih/generated
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .releaseYear(request.getReleaseYear())
                .stock(request.getStock())
                .category(request.getCategory()) // Masukkan objek category hasil temuan repo
                .build();

        // Simpan ke database (Hasil simpan ditampung di variabel 'savedBook')
        Book savedBook = bookRepository.save(book);

        // Kembalikan dalam bentuk Response DTO (Lengkap dengan ID baru)
        return mapToResponse(savedBook);
    }

    // (Read) All data
    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        // Mengubah List<Book> menjadi List<BookResponse>
        return books.stream().map(this::mapToResponse).toList();
    }

    // (Read) by id data
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buku dengan ID " + id + " tidak ditemukan!"));
        return mapToResponse(book);
    }

    // (Update) by id data
    public BookResponse updateBook(Long id, BookRequest request) {
        // Cari dulu bukunya ada atau tidak
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gagal update, buku tidak ditemukan!"));

        // Set ulang data dengan data baru dari Request
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setReleaseYear(request.getReleaseYear());
        book.setStock(request.getStock());
        book.setCategory(request.getCategory());

        // Simpan perubahan
        Book updatedBook = bookRepository.save(book);
        return mapToResponse(updatedBook);
    }

    // (Delete) by id Book
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Gagal hapus, buku tidak ditemukan!");
        }
        bookRepository.deleteById(id);
    }

    // (Search) by isbn, title & author
    public List<BookResponse> searchBooks(String keyword) {
        // Call repository
        List<Book> books = bookRepository.searchBooks(keyword);

        // List<Book> -> List<BookResponse> use helper mapToResponse
        return books.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Helper 2: Entity -> Response (result)
    private BookResponse mapToResponse(Book book) {
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