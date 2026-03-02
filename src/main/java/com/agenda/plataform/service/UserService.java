package com.agenda.plataform.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.UserRole;
import com.agenda.plataform.event.EventPublisher;
import com.agenda.plataform.event.UserCreatedEvent;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.UserRepository;
import com.agenda.plataform.util.specification.UserSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;
    
    @Transactional
    public UserEntity create(UserEntity user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já existe");
        }
        
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        UserEntity created = userRepository.save(user);
        
        eventPublisher.publishUserCreated(UserCreatedEvent.builder()
                .userId(created.getId())
                .email(created.getEmail())
                .name(created.getName())
                .createdAt(created.getCreatedAt())
                .build());
        
        return created;
    }
    
    @Transactional(readOnly = true)
    public UserEntity findById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User não encontrado com ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User não encontrado com email: " + email));
    }
    
    @Transactional(readOnly = true)
    public Page<UserEntity> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<UserEntity> findAll(Specification<UserEntity> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<UserEntity> findByRole(UserRole role, Pageable pageable) {
        Specification<UserEntity> spec = UserSpecifications.byRole(role);
        return userRepository.findAll(spec, pageable);
    }
    
    @Transactional
    public UserEntity update(UUID id, UserEntity userUpdate) {
        UserEntity user = findById(id);
        user.setName(userUpdate.getName());
        user.setActive(userUpdate.getActive());
        user.setUpdatedAt(OffsetDateTime.now());
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteById(UUID id) {
        UserEntity user = findById(id);
        userRepository.delete(user);
    }
}
