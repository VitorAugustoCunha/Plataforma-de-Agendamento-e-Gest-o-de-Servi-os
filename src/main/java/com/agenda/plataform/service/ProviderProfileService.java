package com.agenda.plataform.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.UserRole;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.ProviderProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProviderProfileService {
    
    private final ProviderProfileRepository providerProfileRepository;
    private final UserService userService;
    
    @Transactional
    public ProviderProfileEntity create(UUID userId, ProviderProfileEntity profileData) {
        UserEntity user = userService.findById(userId);
        
        if (user.getRole() != UserRole.PROVIDER) {
            throw new InvalidBusinessRuleException("Usuário não é um PROVIDER");
        }
        
        if (providerProfileRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Provider já possui perfil");
        }
        
        profileData.setUser(user);
        profileData.setCreatedAt(OffsetDateTime.now());
        profileData.setUpdatedAt(OffsetDateTime.now());
        return providerProfileRepository.save(profileData);
    }
    
    @Transactional(readOnly = true)
    public ProviderProfileEntity findById(UUID id) {
        return providerProfileRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProviderProfile não encontrado com ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public ProviderProfileEntity findByUserId(UUID userId) {
        return providerProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("ProviderProfile não encontrado para User ID: " + userId));
    }
    
    @Transactional(readOnly = true)
    public Page<ProviderProfileEntity> findAll(Pageable pageable) {
        return providerProfileRepository.findAll(pageable);
    }
    
    @Transactional
    public ProviderProfileEntity update(UUID id, ProviderProfileEntity profileUpdate) {
        ProviderProfileEntity profile = findById(id);
        
        if (profileUpdate.getBio() != null) {
            profile.setBio(profileUpdate.getBio());
        }
        if (profileUpdate.getLocationText() != null) {
            profile.setLocationText(profileUpdate.getLocationText());
        }
        if (profileUpdate.getMinAdvanceMinutes() != null) {
            profile.setMinAdvanceMinutes(profileUpdate.getMinAdvanceMinutes());
        }
        if (profileUpdate.getCancelWindowMinutes() != null) {
            profile.setCancelWindowMinutes(profileUpdate.getCancelWindowMinutes());
        }
        if (profileUpdate.getSlotStepMinutes() != null) {
            profile.setSlotStepMinutes(profileUpdate.getSlotStepMinutes());
        }
        
        profile.setUpdatedAt(OffsetDateTime.now());
        return providerProfileRepository.save(profile);
    }
}
