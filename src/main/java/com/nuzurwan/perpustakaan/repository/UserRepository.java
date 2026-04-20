package com.nuzurwan.perpustakaan.repository;

import com.nuzurwan.perpustakaan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// interface di repository ibaratkan kontrak kerja
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);   // Ganti dari findByUsername, Optional untuk tidak return null
    boolean existsByEmail(String email);        // Untuk validasi saat daftar
}
