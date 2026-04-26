package com.nuzurwan.perpustakaan.repository;

import com.nuzurwan.perpustakaan.model.Loan;
import com.nuzurwan.perpustakaan.model.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {

    /**
     * 1. Menghitung jumlah pinjaman aktif (BORROWED & OVERDUE).
     * Digunakan untuk validasi kuota maksimal 5 buku di Service.
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.status IN :activeStatuses")
    long countActiveLoans(@Param("userId") String userId,
                          @Param("activeStatuses") List<LoanStatus> activeStatuses);

    /**
     * 2. Mencari transaksi peminjaman yang sedang berjalan berdasarkan User dan Buku.
     * Sangat penting untuk proses 'Return Book' agar tidak salah update data lama.
     */
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.book.id = :bookId AND l.status IN :activeStatuses")
    Optional<Loan> findActiveLoanByUserAndBook(@Param("userId") String userId,
                                               @Param("bookId") String bookId,
                                               @Param("activeStatuses") List<LoanStatus> activeStatuses);
    // Lebih simpel, tanpa perlu menulis SQL/JPQL
    // List<Loan> findAllByStatusAndDueDateBefore(LoanStatus status, LocalDateTime now);

    /**
     * 3. Mengambil riwayat peminjaman milik user tertentu dengan Pagination.
     * Cocok untuk fitur "Peminjaman Saya" di sisi frontend/mobile.
     */
    Page<Loan> findAllByUserId(String userId, Pageable pageable);

    /**
     * 4. Mencari pinjaman yang sudah melewati dueDate tapi statusnya masih BORROWED.
     * Berguna untuk fitur 'Scheduler' atau 'Batch Update' status OVERDUE otomatis.
     */
    @Query("SELECT l FROM Loan l WHERE l.status = :status AND l.dueDate < CURRENT_TIMESTAMP")
    List<Loan> findExpiredLoans(@Param("status") LoanStatus status);

    /**
     * 5. Cek apakah user memiliki minimal satu buku yang sudah OVERDUE.
     * Bisa digunakan untuk blokir pinjaman baru jika ada tanggungan buku telat.
     */
    boolean existsByUserIdAndStatus(String userId, LoanStatus status);

    /**
     * 6. Mengambil semua pinjaman aktif untuk dashboard Admin Perpustakaan.
     */
    Page<Loan> findAllByStatusIn(List<LoanStatus> statuses, Pageable pageable);

    // Hanya ambil yang belum dihapus (Default)
    @Query("SELECT l FROM Loan l WHERE l.deletedAt IS NULL")
    List<Loan> findAllActive();

    // Jika suatu saat butuh melihat sampah (Trash)
    @Query("SELECT l FROM Loan l WHERE l.deletedAt IS NOT NULL")
    List<Loan> findAllDeleted();
}