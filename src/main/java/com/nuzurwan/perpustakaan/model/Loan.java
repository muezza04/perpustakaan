package com.nuzurwan.perpustakaan.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id; // Menggunakan UUID String

    @ManyToOne(fetch = FetchType.LAZY) // saya menggunakan fetchtype lazy untuk menghindari pemanggilan data yg tidak perlu.
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user; // Relasi ke entitas User (Member)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book; // Relasi ke entitas Book (Buku)

    @Column(name = "loan_date", nullable = false)
    private LocalDateTime loanDate; // Tanggal Pinjam

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate; // Batas Kembali (loanDate + 7 hari)

    @Column(name = "return_date")
    private LocalDateTime returnDate; // Tanggal dikembalikan (null jika belum kembali)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false,  length = 20)
    private LoanStatus status; // BORROWED, RETURNED, atau OVERDUE

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Jika null = aktif, jika ada isi = terhapus

    // Helper method untuk inisialisasi awal saat insert
    @PrePersist // Anotasi ini adalah bagian dari Lifecycle Callback, yang bertindak seperti "satpam" atau "asisten otomatis" yang bekerja tepat sebelum data disimpan ke database.
    public void prePersist() {
        // 1. Generate ID otomatis jika belum ada
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }

        // 2. Set tanggal pinjam ke waktu sekarang
        if (this.loanDate == null) {
            this.loanDate = LocalDateTime.now();
        }

        // 3. Bisnis Logika: Otomatis set batas kembali 7 hari kemudian
        if (this.dueDate == null) {
            this.dueDate = this.loanDate.plusDays(7);
        }

        // 4. Set status awal
        if (this.status == null) {
            this.status = LoanStatus.BORROWED;
        }
    }

    @PreUpdate
    public void setLastUpdate() {
        // Otomatis memperbarui waktu setiap kali ada perubahan data
        this.updatedAt = LocalDateTime.now();

        // Logika Bisnis: Jika buku dikembalikan, pastikan status sinkron
        if (this.returnDate != null && this.status == LoanStatus.BORROWED) {
            this.status = LoanStatus.RETURNED;
        }
    }
}