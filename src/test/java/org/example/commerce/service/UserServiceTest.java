package org.example.commerce.service;

import org.example.commerce.dto.request.LoginRequest;
import org.example.commerce.dto.request.RegisterRequest;
import org.example.commerce.dto.response.LoginResponse;
import org.example.commerce.dto.response.RegisterResponse;
import org.example.commerce.entity.RefreshToken;
import org.example.commerce.entity.User;
import org.example.commerce.enums.Role;
import org.example.commerce.exception.AlreadyExistedResource;
import org.example.commerce.exception.ResourceNotFoundException;
import org.example.commerce.mapper.UserMapper;
import org.example.commerce.repository.UserRepository;
import org.example.commerce.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_notExistingUser_returnRegisterResponse() {
        RegisterRequest request = new RegisterRequest("quyethv", "quyethoang@gmail.com", "123654");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        User user = new User();
        user.setId(1);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Role.USER);

        when(userRepository.save(any(User.class))).thenReturn(user);

        RegisterResponse registerResponse = new RegisterResponse(1, "quyethv", "quyethoang@gmail.com", "USER");

        when(userMapper.toRegisterResponse(any(User.class))).thenReturn(registerResponse);

        RegisterResponse result = userService.registerUser(request);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRoles().name(), result.getRoles());

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toRegisterResponse(any(User.class));
    }

    @Test
    void registerUser_existingUser_throwAlreadyExistedResourceException() {
        RegisterRequest request = new RegisterRequest("quyethv", "quyethoang@gmail.com", "123654");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        AlreadyExistedResource exception = assertThrows(
                AlreadyExistedResource.class,
                () -> userService.registerUser(request)
        );

        assertEquals("User already existed", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_validRequest_returnToken() {
        LoginRequest request = new LoginRequest("quyethoang@gmail.com", "123456");
        authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User existingUser = new User(1, "quyethv", "quyethoang@gmail.com", "123456", Role.USER);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        String accessToken = "fake-token";
        RefreshToken refreshToken = new RefreshToken(1, existingUser, accessToken, Instant.parse("2026-12-31T10:00:00.00Z"), false);
        when(jwtService.generateToken(request.getEmail())).thenReturn(accessToken);
        when(refreshTokenService.createRefreshToken(existingUser)).thenReturn(refreshToken);

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(60000)
                .build();
        LoginResponse result = userService.loginUser(request);

        assertEquals(loginResponse.getAccessToken(), result.getAccessToken());
        assertEquals(loginResponse.getRefreshToken(), result.getRefreshToken());

        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }

    @Test
    void loginUser_notFoundEmail_throwResourceNotFoundException() {
        LoginRequest request = new LoginRequest("quyethoang@gmail.com", "123456");
        authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        ResourceNotFoundException exception =assertThrows(
                ResourceNotFoundException.class,
                ()-> userService.loginUser(request)
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }
}
