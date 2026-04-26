package com.nuzurwan.perpustakaan.dto.request;

import com.nuzurwan.perpustakaan.model.BookStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequest {

    /// untuk userid nya bisa diambil dari yg sedang aktic

    @NotBlank(message = "The ID field must be completed")
    private String bookId;

    @NotNull(message = "This field is required")
    private BookStatus bookStatus;
}