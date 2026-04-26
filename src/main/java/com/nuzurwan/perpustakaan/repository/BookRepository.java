package com.nuzurwan.perpustakaan.repository;

import com.nuzurwan.perpustakaan.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Penghubung code java and sql (create variable)
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// penggunaan JPA sudah memberikan tamplate object query standar like: save, findById, findAll, deleteById dll)
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
    boolean existsByIsbn(String isbn); // untuk cek isbn apakah sudah atau belum
}
