package com.agenda.plataform.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.agenda.plataform.dto.user.UserCreateRequest;
import com.agenda.plataform.dto.user.UserResponse;
import com.agenda.plataform.dto.user.UserUpdateRequest;
import com.agenda.plataform.entity.UserEntity;

@Component
public class UserMapper {

    public UserEntity toEntity(UserCreateRequest request) {
        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .role(request.getRole())
                .active(true)
                .build();
    }

    public void updateEntity(UserEntity entity, UserUpdateRequest request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            entity.setPasswordHash(request.getPassword());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }

    public UserResponse toResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<UserResponse> toResponseList(List<UserEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
