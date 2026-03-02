package com.agenda.plataform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agenda.plataform.dto.user.UserCreateRequest;
import com.agenda.plataform.dto.user.UserResponse;
import com.agenda.plataform.dto.user.UserUpdateRequest;
import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.UserRole;
import com.agenda.plataform.mapper.UserMapper;
import com.agenda.plataform.service.UserService;
import com.agenda.plataform.util.specification.UserSpecifications;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        UserEntity user = userMapper.toEntity(request);
        UserEntity created = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        UserEntity user = userService.findById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        
        Page<UserEntity> page = userService.findAll(
                UserSpecifications.byEmail(email)
                        .and(UserSpecifications.byRole(role))
                        .and(UserSpecifications.isActive(active)),
                pageable
        );
        
        return ResponseEntity.ok(userMapper.toResponseList(page.getContent()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        
        UserEntity user = userService.findById(id);
        userMapper.updateEntity(user, request);
        UserEntity updated = userService.update(id, user);
        return ResponseEntity.ok(userMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        // Soft delete: desativa o usuário
        UserEntity user = userService.findById(id);
        user.setActive(false);
        userService.update(id, user);
        return ResponseEntity.noContent().build();
    }
}
