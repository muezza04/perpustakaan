package com.nuzurwan.perpustakaan.controller;

import com.nuzurwan.perpustakaan.dto.request.LoanRequest;
import com.nuzurwan.perpustakaan.dto.response.LoanResponse;
import com.nuzurwan.perpustakaan.dto.response.PagingResponse;
import com.nuzurwan.perpustakaan.dto.response.WebResponse;
import com.nuzurwan.perpustakaan.service.LoanService;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loan Controller")
public class LoanController {

    private final LoanService loanService;

    // ENDPOINT: MEMINJAM BUKU (CREATE)
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Meminjam Book")
    public ResponseEntity<WebResponse<LoanResponse>> create(@Valid @RequestBody LoanRequest request) {
        LoanResponse response = loanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED) // Best Practice: Gunakan 201 Created
                .body(WebResponse.<LoanResponse>builder()
                        .message("Loan transaction successful")
                        .data(response)
                        .build());
    }

    // ENDPOINT: MENGEMBALIKAN BUKU (RETURN)
    @PatchMapping(
            path = "/return/{bookId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Mengembalikan Book")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<WebResponse<LoanResponse>> returnBook(@PathVariable String bookId) {
        LoanResponse response = loanService.returnBook(bookId);
        return ResponseEntity.ok(WebResponse.<LoanResponse>builder()
                .message("Book returned successfully")
                .data(response)
                .build());
    }

    // ENDPOINT: RIWAYAT PEMINJAMAN SAYA (USER PAGING)
    @GetMapping(
            path = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Riwayat Peminjaman for user")
    public ResponseEntity<WebResponse<List<LoanResponse>>> getMyLoans(
            @ParameterObject @PageableDefault(sort = "loanDate") Pageable pageable) {

        Page<LoanResponse> pageResponse = loanService.getMyLoans(pageable);

        return ResponseEntity.ok(WebResponse.<List<LoanResponse>>builder()
                .message("Fetch personal loan history successful")
                .data(pageResponse.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(pageResponse.getNumber())
                        .totalPages(pageResponse.getTotalPages())
                        .totalElements(pageResponse.getTotalElements())
                        .size(pageResponse.getSize())
                        .build())
                .build());
    }

    // ENDPOINT: SEMUA PINJAMAN AKTIF (ADMIN PAGING)
    @GetMapping(
            path = "/active",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "All transaction borrowed & overdue")
    public ResponseEntity<WebResponse<List<LoanResponse>>> getAllActive(
            @ParameterObject @PageableDefault(sort = "dueDate") Pageable pageable) {

        Page<LoanResponse> pageResponse = loanService.getAllActiveLoans(pageable);

        return ResponseEntity.ok(WebResponse.<List<LoanResponse>>builder()
                .message("Fetch all active loans successful")
                .data(pageResponse.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(pageResponse.getNumber())
                        .totalPages(pageResponse.getTotalPages())
                        .totalElements(pageResponse.getTotalElements())
                        .size(pageResponse.getSize())
                        .build())
                .build());
    }

    ///  Refresh apakah sudah ada yg melewati tanggat pengembalian dilakukan secara manual tanpa tunggu jam 00.00 refresh otomatis
    @PostMapping(path = "/status-refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('LIBRARIAN')") // Hanya Admin/Pustakawan yang boleh trigger manual
    @Operation(summary = "Status overdue limit user manual post")
    public ResponseEntity<WebResponse<String>> manualOverdueCheck() {
        loanService.updateOverdueStatus();

        return ResponseEntity.ok(WebResponse.<String>builder()
                .message("Manual overdue status refresh successful")
                .data("Process executed at " + LocalDateTime.now())
                .build());
    }

    @DeleteMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Soft Delete time")
    public ResponseEntity<WebResponse<String>> deleteLoan(@PathVariable String id) {
        loanService.deleteLoanSoft(id);
        return ResponseEntity.ok(
                WebResponse.<String>builder()
                        .message("User deleted soft")
                        .data("OK")
                        .build()
        );
    }
}