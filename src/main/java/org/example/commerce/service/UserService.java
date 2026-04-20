package org.example.commerce.service;

import org.example.commerce.dto.request.LoginRequest;
import org.example.commerce.dto.request.RegisterRequest;
import org.example.commerce.dto.response.LoginResponse;
import org.example.commerce.dto.response.RegisterResponse;
import org.example.commerce.entity.RefreshToken;
import org.example.commerce.entity.User;
import org.example.commerce.enums.Role;
import org.example.commerce.exception.AlreadyExistedResource;
import org.example.commerce.mapper.UserMapper;
import org.example.commerce.repository.UserRepository;
import org.example.commerce.security.CustomUserDetails;
import org.example.commerce.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public RegisterResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail((request.getEmail()))) {
            throw new AlreadyExistedResource("User already existed");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        return userMapper.toRegisterResponse(userRepository.save(user));
    }

    public LoginResponse loginUser(LoginRequest request) {
         Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User user = customUserDetails.getUser();

        String accessToken = jwtService.generateToken(request.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }

    public LoginResponse refreshAccessToken(String refreshTokenString) {
        RefreshToken oldToken = refreshTokenService.validateRefreshToken(refreshTokenString);
        refreshTokenService.revokeToken(refreshTokenString);

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(oldToken.getUser());
        String newAccessToken = jwtService.generateToken(oldToken.getUser().getEmail());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }
}
