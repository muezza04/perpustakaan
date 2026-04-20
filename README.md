
Password di-encode menggunakan BCrypt (Standar Industri Spring Boot) </br>
Methode Cryptographic Hashing (package config)

Service - > Repository: Service memberikan perintah (Simpan, Cari, Hapus). </br>
Repository - > Database: Repository menerjemahkan perintah Java menjadi perintah SQL. </br>
Service < - > Model: Service mengisi atau mengambil data dari Model (Entity). </br>
Model -> Database: Model hanyalah "cerminan" dari tabel database.

DatabaseSeeder = > bertujuan untuk membuat admin langsung ketika aplikasi dijalankan. </br>
Karena endpoint api yg terkunci karena SecurityConfig