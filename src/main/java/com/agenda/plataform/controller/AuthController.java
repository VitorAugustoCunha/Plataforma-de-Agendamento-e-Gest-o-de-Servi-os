package com.agenda.plataform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agenda.plataform.dto.auth.AuthRequest;
import com.agenda.plataform.dto.auth.AuthResponse;
import com.agenda.plataform.dto.user.UserCreateRequest;
import com.agenda.plataform.dto.user.UserResponse;
import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.mapper.UserMapper;
import com.agenda.plataform.security.JwtProvider;
import com.agenda.plataform.security.TokenBlacklistService;
import com.agenda.plataform.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de registro, login e autenticação")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserMapper userMapper;
    
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário (Cliente, Provedor ou Admin)")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreateRequest request) {
        UserEntity user = userMapper.toEntity(request);
        UserEntity created = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(created));
    }
    
    @Operation(summary = "Fazer login", description = "Autentica um usuário e retorna um token JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
              
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            UserEntity user = userService.findByEmail(request.getEmail());
            
            String token = jwtProvider.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole().name()
            );
            
            return ResponseEntity.ok(AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole())
                    .build());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .error("Email ou senha inválidos")
                            .build());
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            java.time.Instant expiration = jwtProvider.getExpiration(token);
            if (expiration != null) {
                tokenBlacklistService.revoke(token, expiration);
            }
        }
        return ResponseEntity.noContent().build();
    }
}
