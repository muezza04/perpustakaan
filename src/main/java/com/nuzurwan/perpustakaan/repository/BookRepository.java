package com.nuzurwan.perpustakaan.repository;

import com.nuzurwan.perpustakaan.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Kosong saja sudah cukup,
    // karena JpaRepository sudah punya semua fungsi standar CRUD.
}
