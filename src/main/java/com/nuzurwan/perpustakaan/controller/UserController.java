package com.nuzurwan.perpustakaan.controller;

import com.nuzurwan.perpustakaan.dto.request.UserCreateRequest;
import com.nuzurwan.perpustakaan.dto.request.UserUpdateRequest;
import com.nuzurwan.perpustakaan.dto.response.UserResponse;
import com.nuzurwan.perpustakaan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create new User")
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Read All User")
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Read by Id User")
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update by Id User")
    public ResponseEntity<UserResponse> update(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    // Hard Delete
    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Delete by Id User permanent")
    public ResponseEntity<UserResponse> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // Soft Delete
    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate delete user")
    public ResponseEntity<UserResponse> deactivate(@PathVariable String id) {
        userService.deleteSoftUser(id);
        return ResponseEntity.noContent().build();
    }
}
