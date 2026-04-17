package org.example.commerce.service;

import org.example.commerce.entity.RefreshToken;
import org.example.commerce.entity.User;
import org.example.commerce.enums.Role;
import org.example.commerce.exception.ResourceNotFoundException;
import org.example.commerce.repository.RefreshTokenRepository;
import org.example.commerce.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void createRefreshToken_validRequest_returnRefreshToken() {
        User user = new User(1, "quyethv", "quyethoang@gmail.com", "123456", Role.USER);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(jwtService.getRefreshTokenExpiryDate());
        refreshToken.setRevoked(false);

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertNotNull(result);
        assertEquals(refreshToken.getUser(), result.getUser());
        assertEquals(refreshToken.getToken(), result.getToken());

        verify(refreshTokenRepository, times(1)).deleteByUser(user);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));

    }

    @Test
    void validateRefreshToken_validToken_returnRefreshToken() {
        String token = "valid-refresh-token";
        User user = new User(1, "quyethv", "quyethoang@gmail.com", "123456", Role.USER);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
        refreshToken.setRevoked(false);

        when(refreshTokenRepository.findWithUserByToken(token)).thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.validateRefreshToken(token);

        assertNotNull(result);
        assertEquals(token, result.getToken());
        verify(refreshTokenRepository, times(1)).findWithUserByToken(token);
    }

    @Test
    void validateRefreshToken_tokenNotFound_throwResourceNotFoundException() {
        String token = "missing-refresh-token";
        when(refreshTokenRepository.findWithUserByToken(token)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> refreshTokenService.validateRefreshToken(token)
        );

        assertEquals("Refresh token not found", exception.getMessage());
        verify(refreshTokenRepository, times(1)).findWithUserByToken(token);
    }

    @Test
    void validateRefreshToken_revokedToken_throwResourceNotFoundException() {
        String token = "revoked-refresh-token";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
        refreshToken.setRevoked(true);

        when(refreshTokenRepository.findWithUserByToken(token)).thenReturn(Optional.of(refreshToken));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> refreshTokenService.validateRefreshToken(token)
        );

        assertEquals("Refresh token is expired or revoked", exception.getMessage());
        verify(refreshTokenRepository, times(1)).findWithUserByToken(token);
    }

    @Test
    void validateRefreshToken_expiredToken_throwResourceNotFoundException() {
        String token = "expired-refresh-token";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().minusSeconds(60));
        refreshToken.setRevoked(false);

        when(refreshTokenRepository.findWithUserByToken(token)).thenReturn(Optional.of(refreshToken));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> refreshTokenService.validateRefreshToken(token)
        );

        assertEquals("Refresh token is expired or revoked", exception.getMessage());
        verify(refreshTokenRepository, times(1)).findWithUserByToken(token);
    }

    @Test
    void revokeToken_validToken_setRevokedTrueAndSave() {
        String token = "active-refresh-token";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setRevoked(false);

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        refreshTokenService.revokeToken(token);

        assertEquals(true, refreshToken.getRevoked());
        verify(refreshTokenRepository, times(1)).findByToken(token);
        verify(refreshTokenRepository, times(1)).save(refreshToken);
    }

    @Test
    void revokeToken_tokenNotFound_throwResourceNotFoundException() {
        String token = "missing-refresh-token";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> refreshTokenService.revokeToken(token)
        );

        assertEquals("Refresh token not found", exception.getMessage());
        verify(refreshTokenRepository, times(1)).findByToken(token);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }
}
