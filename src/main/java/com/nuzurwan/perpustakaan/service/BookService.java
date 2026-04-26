package com.nuzurwan.perpustakaan.service;

import com.nuzurwan.perpustakaan.dto.request.BookCreateRequest;
import com.nuzurwan.perpustakaan.dto.request.BookUpdateRequest;
import com.nuzurwan.perpustakaan.dto.response.BookResponse;
import com.nuzurwan.perpustakaan.dto.response.BookStatusResponse;
import com.nuzurwan.perpustakaan.model.Book;
import com.nuzurwan.perpustakaan.model.BookStatus;
import com.nuzurwan.perpustakaan.model.Category;
import com.nuzurwan.perpustakaan.repository.BookRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.NonNull; // untuk memastikan data yang diterima(request) tidak null
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

// respon status error
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor // Otomatis menghubungkan Repository (Dependency Injection)
public class BookService {

    private final BookRepository bookRepository;

    // (Add) data
    /// Jangan lupa untuk testing data terutama untuk isbn
    @Transactional
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

        // --- 2. PROSES MAPPING & SAVE ---

        // Manual Request to Entity bersifat khusus
        Book book = Book.builder()
                .isbn(finalIsbn) // Gunakan ISBN yang sudah bersih/generated
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .releaseYear(request.getReleaseYear())
                .status(request.getStatus())
                .category(request.getCategory()) // tipe data string request menjadi category
                .build();

        // Simpan ke database (Hasil simpan ditampung di variabel 'savedBook')
        Book savedBook = bookRepository.save(book);

        // Kembalikan dalam bentuk Entity -> Response DTO (Lengkap dengan ID baru)
        return mapToResponse(savedBook);
    }

    // (Read) All data
    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);

        return books.map(this::mapToResponse);
    }

    // (Read) by id data
    @Transactional(readOnly = true)
    public BookResponse getBookById(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return mapToResponse(book);
    }

    // (Update) by id data
    @Transactional
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


        // 3. Update field lainnya
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setReleaseYear(request.getReleaseYear());
        book.setStatus(request.getStatus());
        book.setCategory(request.getCategory()); // wajib memiliki validari manual enum sebelum string dirubah ke categry

        return mapToResponse(bookRepository.save(book));
    }

    // (Delete) by id Book
    @Transactional
    public void deleteBook(String id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found!");
        }
        bookRepository.deleteById(id);
    }

    /// (Delete soft) from LIBRARIAN
    @Transactional
    public BookStatusResponse deleteSoft(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found!"));

        // VALIDASI: Jangan hapus jika ada transaksi aktif
        if (book.getStatus() == BookStatus.BORROWED || book.getStatus() == BookStatus.RESERVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot discard book: The book is currently " + book.getStatus());
        }

        // Eksekusi Soft Delete
        book.setStatus(BookStatus.DISCARDED);

        bookRepository.save(book);

        return BookStatusResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .status(book.getStatus())
                .build();
    }

    // (Search) by isbn, title & author
    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(String keyword, Category category, Pageable pageable) {
        Specification<Book> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Logika Keyword (Search di firstName, lastName, dan email)
            if (keyword != null && !keyword.isBlank()) {
                String lowerKeyword = "%" + keyword.toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("isbn")), lowerKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), lowerKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), lowerKeyword)
                );
                predicates.add(keywordPredicate);
            }

            // 2. Filter berdasarkan category (Jika ada)
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Book> books = bookRepository.findAll(specification, pageable);

        // Transformasi Page<User> menjadi Page<UserResponse>
        return books.map(this::mapToResponse);
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
                .status(book.getStatus())
                .category(book.getCategory())
                .build();
    }
}