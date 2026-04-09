package com.nuzurwan.perpustakaan.controller;

import com.nuzurwan.perpustakaan.dto.request.CreateBookRequest;
import com.nuzurwan.perpustakaan.dto.request.UpdateBookRequest;
import com.nuzurwan.perpustakaan.dto.response.BookResponse;
import com.nuzurwan.perpustakaan.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books") // Alamat URL utama untuk buku
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Book Add")
    public ResponseEntity<BookResponse> create(@Valid @RequestBody CreateBookRequest request) {
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Book Read All")
    public ResponseEntity<List<BookResponse>> getAll() {
        List<BookResponse> response = bookService.getAllBooks();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Book Find by Id")
    public ResponseEntity<BookResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Book Update")
    public ResponseEntity<BookResponse> update(@PathVariable String id, @Valid @RequestBody UpdateBookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Book Delete by Id")
    public ResponseEntity<String> delete(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Buku dengan ID " + id + " berhasil dihapus!");
    }

    @GetMapping("/search")
    @Operation(summary = "Search (ISBN/Judul/Penulis)")
    public ResponseEntity<List<BookResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(bookService.searchBooks(keyword));
    }
}