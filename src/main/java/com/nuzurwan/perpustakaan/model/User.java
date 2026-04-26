package com.nuzurwan.perpustakaan.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    @Column(updatable = false, nullable = false, length = 36) // generate uuid pasti 36
    private String id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = true, length = 50)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String password;

    // untuk role dan status terutama berfungsi untuk create akum

    @Enumerated(EnumType.STRING) // secara default tanpa batasan length, memberikan varchar 255
    @Builder.Default
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.GUEST;

    /* @Builder.Default untuk membarikan nilai default pada lombok untuk memberi nilai tetap,
       karena disini ditetap isActive bernilai true.
       jika tidak menggunakannya lombok secara otomatis akan memberikan nilai false(boolean) null(Boolean) ketika builder di service
    */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false,  length = 20)
    private UserStatus status = UserStatus.PENDING;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Loan> loans;
}