package com.nuzurwan.perpustakaan.controller;

import com.nuzurwan.perpustakaan.dto.request.BookCreateRequest;
import com.nuzurwan.perpustakaan.dto.request.BookUpdateRequest;
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
    @Operation(summary = "Create new Book")
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookCreateRequest request) {
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get All Book")
    public ResponseEntity<List<BookResponse>> getAll() {
        List<BookResponse> response = bookService.getAllBooks();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by Id Book")
    public ResponseEntity<BookResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update by Id Book")
    public ResponseEntity<BookResponse> update(@PathVariable String id, @Valid @RequestBody BookUpdateRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete by Id Book")
    public ResponseEntity<String> delete(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build(); // mengirim status 204
    }

    @GetMapping("/search")
    @Operation(summary = "Search (ISBN/Judul/Penulis) Book")
    public ResponseEntity<List<BookResponse>> search(
            @RequestParam(required = false) String keyword) { //
        return ResponseEntity.ok(bookService.searchBooks(keyword));
    }
}