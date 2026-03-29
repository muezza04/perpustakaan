package com.nuzurwan.perpustakaan.repository;

import com.nuzurwan.perpustakaan.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Penghubung code java and sql (create variable)
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn); // untuk cek isbn apakah sudah atau belum
    // Query untuk mencari di 3 kolom sekaligus
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(@Param("keyword") String keyword);
}
