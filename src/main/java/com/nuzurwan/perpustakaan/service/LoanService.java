package com.nuzurwan.perpustakaan.service;

import com.nuzurwan.perpustakaan.dto.request.LoanRequest;
import com.nuzurwan.perpustakaan.dto.response.LoanResponse;
import com.nuzurwan.perpustakaan.model.*;
import com.nuzurwan.perpustakaan.repository.BookRepository;
import com.nuzurwan.perpustakaan.repository.LoanRepository;
import com.nuzurwan.perpustakaan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j // untuk menghasilkan (generate) logger secara otomatis pada kelas Java
@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    private static final List<LoanStatus> ACTIVE_STATUSES = List.of(LoanStatus.BORROWED, LoanStatus.OVERDUE);

    /**
     * Helper untuk mengambil user yang sedang login.
     * Ini mempermudah migrasi ke JWT nantinya karena logika "siapa yang login"
     * terpusat di satu method private ini.
     */
    private User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Session Not Found"));
    }

    // --- 1. PROSES PEMINJAMAN (CREATE) ---
    @Transactional
    public LoanResponse createLoan(LoanRequest request) {
        User user = getCurrentAuthenticatedUser();

        // Validasi: Akun harus ACTIVE
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Access Denied User: {} is trying to borrow with status: {}", user.getId(), user.getStatus());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        // Validasi Query 5: Cek jika ada buku yang telat (OVERDUE)
        if (loanRepository.existsByUserIdAndStatus(user.getId(), LoanStatus.OVERDUE)) {
            log.warn("Loan Blocked User: {} has overdue books", user.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please return overdue books before borrowing new ones");
        }

        // Validasi Query 1: Cek Limit 5 Buku
        if (loanRepository.countActiveLoans(user.getId(), ACTIVE_STATUSES) >= 5) {
            log.info("Loan Limit Reached User: {} hit maximum quota", user.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum loan limit (5 books) reached");
        }

        // Validasi Buku
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (book.getStatus() != BookStatus.AVAILABLE) {
            log.warn("Loan Status Book: Book {} is not available status: {}", request.getBookId(), book.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is currently " + book.getStatus());
        }

        // Eksekusi: Update Book Status & Save Loan
        book.setStatus(BookStatus.BORROWED);
        bookRepository.save(book);
        log.info("Book set status: {}", book.getStatus());

        Loan loan = Loan.builder()
                .user(user)
                .book(book)
                .build();

        loanRepository.save(loan);
        log.info("Loan Created id: {}", loan.getId());
        return mapToResponse(loan);
    }

    // --- 2. PROSES PENGEMBALIAN (RETURN) ---
    @Transactional
    public LoanResponse returnBook(String bookId) {
        User user = getCurrentAuthenticatedUser();

        // Query 2: Mencari transaksi peminjaman yang sedang aktif
        Loan loan = loanRepository.findActiveLoanByUserAndBook(user.getId(), bookId, ACTIVE_STATUSES)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active loan record found for this book"));

        // Update Tanggal Kembali (Trigger @PreUpdate untuk ubah status ke RETURNED)
        loan.setReturnDate(LocalDateTime.now());
        log.info("Return Book updated successful id: {}", loan.getId());

        // Update Status Buku di Rak
        Book book = loan.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);
        log.info("Return book update partial status: {} successful", book.getStatus());

        loanRepository.save(loan);
        return mapToResponse(loan);
    }

    // --- 3. DASHBOARD USER: RIWAYAT PEMINJAMAN (PAGING) ---
    @Transactional(readOnly = true)
    public Page<LoanResponse> getMyLoans(Pageable pageable) {
        User user = getCurrentAuthenticatedUser();
        // Query 3: Ambil semua riwayat user dengan Paging
        return loanRepository.findAllByUserId(user.getId(), pageable).map(this::mapToResponse);
    }

    // --- 4. DASHBOARD ADMIN: MONITOR SEMUA PINJAMAN AKTIF ---
    @Transactional(readOnly = true)
    public Page<LoanResponse> getAllActiveLoans(Pageable pageable) {
        // Query 6: Ambil semua BORROWED & OVERDUE untuk Admin
        return loanRepository.findAllByStatusIn(ACTIVE_STATUSES, pageable)
                .map(this::mapToResponse);
    }

    // --- 5. SCHEDULER / BATCH: UPDATE STATUS TELAT ---
    @Transactional
    public void updateOverdueStatus() {
        // Query 4: Cari yang BORROWED tapi sudah lewat tenggat
        List<Loan> expiredLoans = loanRepository.findExpiredLoans(LoanStatus.BORROWED);

        if (expiredLoans.isEmpty()) {
            log.info("No expired loans found today.");
            return; // Keluar dari method lebih awal
        }

        expiredLoans.forEach(loan -> {
            loan.setStatus(LoanStatus.OVERDUE);
            log.warn("Loan ID {} is now OVERDUE", loan.getId());
        });

        // melakukan seveAll untuk update data secara bacth karena disini tipe data List
        loanRepository.saveAll(expiredLoans);
    }

    /// (Soft Delete menggunakan nilai null dan time)
    @Transactional
    public void deleteLoanSoft(String id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        // Set timestamp penghapusan
        loan.setDeletedAt(LocalDateTime.now());

        // Simpan perubahan
        loanRepository.save(loan);
        log.info("Loan ID: {} has been soft-deleted", id);
    }

    // --- HELPER MAPPER ---
    private LoanResponse mapToResponse(Loan loan) {
        log.info("Loan ID: {} entity->response", loan.getId());
        return LoanResponse.builder()
                .id(loan.getId())
                .userId(loan.getUser().getId())
                .userName(loan.getUser().getFirstName() + " " + loan.getUser().getLastName())
                .bookId(loan.getBook().getId())
                .bookTitle(loan.getBook().getTitle())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .status(loan.getStatus().name())
                .build();
    }
}