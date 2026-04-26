package com.nuzurwan.perpustakaan.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkDeleteRequest {

    @NotEmpty(message = "List of IDs must not be empty")
    private List<String> ids;
}
