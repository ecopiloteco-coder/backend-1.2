package com.ecopilot.user.controller;

import com.ecopilot.user.dto.ApiResponse;
import com.ecopilot.user.dto.LoginRequest;
import com.ecopilot.user.dto.LoginResponse;
import com.ecopilot.user.dto.SignupRequest;
import com.ecopilot.user.dto.UserDTO;
import com.ecopilot.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticate a user via Keycloak.
     * Returns access token, refresh token, and user info.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .success(true)
                .data(authService.login(request))
                .build());
    }

    /**
     * Register a new user in Keycloak and the local database.
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDTO>> signup(@Valid @RequestBody SignupRequest request) {
        log.info("Signup request received for email: {}", request.getEmail());
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .success(true)
                .data(authService.signup(request))
                .build());
    }

    /**
     * Refresh an access token using a Keycloak refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .success(true)
                .data(authService.refresh(refreshToken))
                .build());
    }
}
