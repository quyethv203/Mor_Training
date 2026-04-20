package org.example.commerce.controller;
import jakarta.validation.Valid;
import org.example.commerce.dto.request.LoginRequest;
import org.example.commerce.dto.request.RegisterRequest;
import org.example.commerce.dto.response.ApiResponse;
import org.example.commerce.dto.response.LoginResponse;
import org.example.commerce.dto.response.RegisterResponse;
import org.example.commerce.service.RefreshTokenService;
import org.example.commerce.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse registerResponse = userService.registerUser(request);
        ApiResponse<RegisterResponse> userResponseApiResponse = ApiResponse.<RegisterResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Registered successfully")
                .data(registerResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseApiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = userService.loginUser(request);
        ApiResponse<LoginResponse> loginResponseApiResponse = ApiResponse.<LoginResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Login successfully")
                .data(loginResponse)
                .build();
        return ResponseEntity.ok(loginResponseApiResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody String refreshToken) {
        LoginResponse newTokens = userService.refreshAccessToken(refreshToken);
        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Token refreshed successfully")
                .data(newTokens)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Logout successfully")
                .data("Token revoked")
                .build();
        return ResponseEntity.ok(response);
    }

}
