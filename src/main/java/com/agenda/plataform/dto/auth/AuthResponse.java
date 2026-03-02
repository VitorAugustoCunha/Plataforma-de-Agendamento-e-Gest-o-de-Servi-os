package com.agenda.plataform.dto.auth;

import java.util.UUID;

import com.agenda.plataform.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    
    private String token;
    private UUID userId;
    private String email;
    private String name;
    private UserRole role;
    private String error;
}
