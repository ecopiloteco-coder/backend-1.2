package com.ecopilot.user.controller;

import com.ecopilot.user.dto.UserDTO;
import com.ecopilot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<com.ecopilot.user.dto.ApiResponse<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(com.ecopilot.user.dto.ApiResponse.<List<UserDTO>>builder()
                .success(true)
                .data(userService.getAllUsers())
                .build());
    }

    @GetMapping("/roles")
    public ResponseEntity<com.ecopilot.user.dto.ApiResponse<com.ecopilot.user.entity.UserRole[]>> getRoles() {
        return ResponseEntity.ok(com.ecopilot.user.dto.ApiResponse.<com.ecopilot.user.entity.UserRole[]>builder()
                .success(true)
                .data(com.ecopilot.user.entity.UserRole.values())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.ecopilot.user.dto.ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(com.ecopilot.user.dto.ApiResponse.<UserDTO>builder()
                .success(true)
                .data(userService.getUserById(id))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<com.ecopilot.user.dto.ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(com.ecopilot.user.dto.ApiResponse.<UserDTO>builder()
                .success(true)
                .data(userService.updateUser(id, userDTO))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<com.ecopilot.user.dto.ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(com.ecopilot.user.dto.ApiResponse.<Void>builder()
                .success(true)
                .message("User deleted successfully")
                .build());
    }
}
