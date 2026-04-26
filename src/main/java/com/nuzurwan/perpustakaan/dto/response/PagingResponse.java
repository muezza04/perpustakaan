package com.nuzurwan.perpustakaan.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PagingResponse {
    private Integer currentPage;
    private Integer totalPages;
    private Long totalElements;
    private Integer size;
}