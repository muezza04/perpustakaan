package com.nuzurwan.perpustakaan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Builder
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /* @Builder.Default untuk membarikan nilai default pada lombok untuk memberi nilai tetap,
       karena disini ditetap isActive bernilai true.
       jika tidak menggunakannya lombok secara otomatis akan memberikan nilai false(boolean) null(Boolean) ketika builder di service
    */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}