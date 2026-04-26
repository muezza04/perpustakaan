package com.nuzurwan.perpustakaan.repository;

import com.nuzurwan.perpustakaan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// interface di repository ibaratkan kontrak kerja
@Repository
// Secara default, JpaRepository hanya mengerti query dasar (seperti save, findById). Untuk menjalankan Specification, kamu harus melakukan multiple inheritance (mewarisi dua interface sekaligus).
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);   // Ganti dari findByUsername, Optional untuk tidak return null
    boolean existsByEmail(String email);        // Untuk validasi saat daftar
}
