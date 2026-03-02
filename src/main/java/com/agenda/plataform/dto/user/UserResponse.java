package com.agenda.plataform.dto.user;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.agenda.plataform.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    
    private UUID id;
    private String name;
    private String email;
    private UserRole role;
    private Boolean active;
    private OffsetDateTime createdAt;
}
