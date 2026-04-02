package com.proptech.backend.api.controller;

import com.proptech.backend.api.dto.AuthResponse;
import com.proptech.backend.api.dto.LoginRequest;
import com.proptech.backend.api.dto.RegisterRequest;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.repository.UserRepository;
import com.proptech.backend.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userOpt.get();
        String provided = loginRequest.getPassword() == null ? "" : loginRequest.getPassword();

        if (!passwordEncoder.matches(provided, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(buildAuthResponse(user));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        UserEntity newUser = new UserEntity();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setName(registerRequest.getName());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setTrustScore(0);
        newUser.setIsVerified(false);
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(buildAuthResponse(newUser));
    }

    private AuthResponse buildAuthResponse(UserEntity user) {
        Map<String, Object> claims = Map.of(
                "roles", List.of("USER"),
                "email", user.getEmail()
        );
        String token = jwtService.generateToken(claims, user);

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setExpiresIn(86400);
        return resp;
    }
}
