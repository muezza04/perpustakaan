package com.nuzurwan.perpustakaan.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


// wrapper, untuk handle errors ada perubahan pada globalexceptionhandler
// change on (All type Response -> WebResponse) data response will become one
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // mengontrol kapan sebuah field harus ditampilkan atau disembunyikan
public class WebResponse<T> {
    private String message;

    private T data;

    private String errors;
}
