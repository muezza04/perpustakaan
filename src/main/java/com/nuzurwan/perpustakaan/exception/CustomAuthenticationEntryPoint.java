package com.nuzurwan.perpustakaan.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuzurwan.perpustakaan.dto.response.WebResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 1. Set Status ke 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 2. Buat format WebResponse
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .message("Akses Ditolak: Silakan login terlebih dahulu untuk mengakses resource ini.")
                .data(null)
                .build();

        // 3. Konversi object ke JSON String
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(webResponse);

        response.getWriter().write(jsonResponse);
    }
}