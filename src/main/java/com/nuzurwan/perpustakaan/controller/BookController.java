package com.nuzurwan.perpustakaan.controller;

import com.nuzurwan.perpustakaan.dto.request.BookCreateRequest;
import com.nuzurwan.perpustakaan.dto.request.BookUpdateRequest;
import com.nuzurwan.perpustakaan.dto.response.BookResponse;
import com.nuzurwan.perpustakaan.dto.response.BookStatusResponse;
import com.nuzurwan.perpustakaan.dto.response.PagingResponse;
import com.nuzurwan.perpustakaan.dto.response.WebResponse;
import com.nuzurwan.perpustakaan.model.Category;
import com.nuzurwan.perpustakaan.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books") // Alamat URL utama untuk buku
@RequiredArgsConstructor
@Tag(name = "Book Controller")
public class BookController {

    private final BookService bookService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    @Operation(summary = "Create new Book")
    public ResponseEntity<WebResponse<BookResponse>> create(@Valid @RequestBody BookCreateRequest request) {
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                WebResponse.<BookResponse>builder()
                        .message("Book created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get All Book")
    public ResponseEntity<WebResponse<List<BookResponse>>> getAll(
            @ParameterObject @PageableDefault(sort = "title") Pageable pageable) {

        Page<BookResponse> pageResult = bookService.getAllBooks(pageable);
        String message = pageResult.isEmpty() ? "Data not found" : "Successfully fetched all books";

        return ResponseEntity.ok(
                WebResponse.<List<BookResponse>>builder()
                        .message(message)
                        .data(pageResult.getContent())
                        .paging(PagingResponse.builder()
                                .currentPage(pageResult.getNumber())
                                .totalPages(pageResult.getTotalPages())
                                .totalElements(pageResult.getTotalElements())
                                .size(pageResult.getSize())
                                .build())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get by Id Book")
    public ResponseEntity<WebResponse<BookResponse>> getById(@PathVariable String id) {
        BookResponse response = bookService.getBookById(id);
        return ResponseEntity.ok(
                WebResponse.<BookResponse>builder()
                        .message("User found")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update by Id Book")
    public ResponseEntity<WebResponse<BookResponse>> update(@PathVariable String id, @Valid @RequestBody BookUpdateRequest request) {
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(
                WebResponse.<BookResponse>builder()
                        .message("User found")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Delete by Id Book permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(
                WebResponse.<String>builder()
                        .message("Book deleted successfully")
                        .data("OK")
                        .build()
        );
    }

    @DeleteMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Melakukan delete soft, perubahan pada status")
    @PreAuthorize("hasRole('LIBRARIAN')") // Hirarki: Admin otomatis bisa akses
    public ResponseEntity<WebResponse<BookStatusResponse>> deleteSoft(@PathVariable String id) {

        BookStatusResponse response = bookService.deleteSoft(id);

        return ResponseEntity.ok(WebResponse.<BookStatusResponse>builder()
                .message("Successfully DISCARDED books status")
                .data(response)
                .build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search (ISBN/Judul/Penulis) Book")
    public ResponseEntity<WebResponse<List<BookResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            /// menggunakan anotasi @PrameterObject untuk membuat pageable tidak required di swagger
            @ParameterObject @PageableDefault(sort = "title") Pageable pageable) {

        Page<BookResponse> pageResult = bookService.searchBooks(keyword, category, pageable);

        // Best Practice: Berikan pesan yang membantu User
        String message = pageResult.isEmpty() ? "Data not found" : "Successfully retrieved user data";

        return ResponseEntity.ok(
                WebResponse.<List<BookResponse>>builder()
                        .message(message)
                        .data(pageResult.getContent()) // Hanya ambil kontennya saja (List)
                        .paging(PagingResponse.builder() // Rakit metadata halamannya sendiri
                                .currentPage(pageResult.getNumber())
                                .totalPages(pageResult.getTotalPages())
                                .totalElements(pageResult.getTotalElements())
                                .size(pageResult.getSize())
                                .build())
                        .build()
        );
    }
}