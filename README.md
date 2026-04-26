
Password di-encode menggunakan BCrypt (Standar Industri Spring Boot) </br>
Methode Cryptographic Hashing (package config)

Service - > Repository: Service memberikan perintah (Simpan, Cari, Hapus). </br>
Repository - > Database: Repository menerjemahkan perintah Java menjadi perintah SQL. </br>
Service < - > Model: Service mengisi atau mengambil data dari Model (Entity). </br>
Model -> Database: Model hanyalah "cerminan" dari tabel database.
</br>

kenapa versi openapi di pom.xml dan di swagger berbeda? : Hal tersebut dikarenakan adanya perbedaan antara Software Versioning dan Specification Versioning. Versi 2.3.0 di dalam pom.xml merujuk pada rilis library springdoc-openapi-starter-webmvc-ui yang saya gunakan. Sedangkan OpenAPI 3 yang tampil di Swagger UI merujuk pada standar spesifikasi dokumentasi API internasional yang diadopsi oleh library tersebut.
</br>

"Kenapa tampilan schema Anda berbeda dengan hasil execute-nya?" : Perbedaan tersebut mencerminkan pemisahan antara API Documentation Contract dan Runtime Execution Result.
</br>

Mengatur length dapat memberikan manfaat pada database dan tidak memberatkan database searching kelebihannya
</br>

Unit Testing, lebih mudah digunakan ketika reques ada builder
</br>

DatabaseSeeder = > bertujuan untuk membuat admin langsung ketika aplikasi dijalankan. </br>
Karena endpoint api yg terkunci karena SecurityConfig
permasalahan login, spring security - database - swagger = pop-up ga jelas - header response no cookie - header response login improv dan pahami </br>
// Garis merah yg harus dipahami
AuhtController, DatabaseSeeder, SecurityConfig, customAuthenticationEntry, AuthService, application.properties </br>
permasalahan dengan response security logout dan endpoint api logout in authcontroller
</br>

pemahamana tentang globalexception handleJsonError
password security hight belum dilakukan, perbaikan pada metode updatemail di service
</br>

Scheduler package, class, method updateoverduestatus in class service loan, file utama or main
</br>

Delete soft bulk for buku, delete bulk permanent/soft for user admin, 
Delete Book perlu diperbaiki jika bukunya di pinjam tidak bisa delete buku

pelajari kegunaan @Slf4j
</br>

case dimana ketika mengembalikan book, sebagai perpustakaan atau staf librarian memastikan status book
apakah bukunya setelah dikembalikan rusak atau bagaimana(not otomatis set AVAILABLE). LoanServices.returnBook
BookStatus.RESERVED perlu di pikirkan kembali.</br>
Tahap   -   Aktor   -   Status Buku  -  Status Loan
Booking	    Member	    RESERVED	    RESERVED
Pick-up 	Librarian	BORROWED	    BORROWED
Return	    Librarian	AVAILABLE	    RETURNED