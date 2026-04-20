package com.nuzurwan.perpustakaan.controller;

import com.nuzurwan.perpustakaan.dto.request.UserCreateRequest;
import com.nuzurwan.perpustakaan.dto.request.UserUpdateRequest;
import com.nuzurwan.perpustakaan.dto.response.UserResponse;
import com.nuzurwan.perpustakaan.dto.response.WebResponse;
import com.nuzurwan.perpustakaan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(
            // MediaType konstanta (variabel tetap) yang disediakan oleh Spring untuk menghindari kesalahan pengetikan (typo).
            consumes = MediaType.APPLICATION_JSON_VALUE, // Hanya terima JSON
            produces = MediaType.APPLICATION_JSON_VALUE  // Pasti kirim JSON
    )
    @Operation(summary = "Create new User")
    public ResponseEntity<WebResponse<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                WebResponse.<UserResponse>builder()
                        .message("User created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Read All User")
    public ResponseEntity<WebResponse<List<UserResponse>>> getAll() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(
                WebResponse.<List<UserResponse>>builder()
                        .message("Successfully fetched all users")
                        .data(response)
                        .build()
        );
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Read by Id User")
    public ResponseEntity<WebResponse<UserResponse>> getById(@PathVariable String id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(
                WebResponse.<UserResponse>builder()
                        .message("User found")
                        .data(response)
                        .build()
        );
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update by Id User")
    public ResponseEntity<WebResponse<UserResponse>> update(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(
                WebResponse.<UserResponse>builder()
                        .message("User updated successfully")
                        .data(response)
                        .build()
        );
    }

    // Hard Delete
    @DeleteMapping(value = "/{id}/permanent", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete by Id User permanent")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                WebResponse.<String>builder()
                        .message("User permanently deleted")
                        .data("OK")
                        .build()
        );
    }

    // Soft Delete
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deactivate delete user")
    public ResponseEntity<WebResponse<String>> deactivate(@PathVariable String id) {
        userService.deleteSoftUser(id);
        return ResponseEntity.ok(
                WebResponse.<String>builder()
                        .message("User deactivated successfully (Soft Delete)")
                        .data("OK")
                        .build()
        );
    }
}
