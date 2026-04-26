package com.nuzurwan.perpustakaan.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanResponse {
    private String id;
    private String userId;
    private String userName;
    private String bookId;
    private String bookTitle;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private String status;
}
