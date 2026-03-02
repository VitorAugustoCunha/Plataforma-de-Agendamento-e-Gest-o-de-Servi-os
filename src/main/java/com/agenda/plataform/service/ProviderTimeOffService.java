package com.agenda.plataform.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.entity.ProviderTimeOffEntity;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.ProviderTimeOffRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProviderTimeOffService {
    
    private final ProviderTimeOffRepository providerTimeOffRepository;
    private final ProviderProfileService providerProfileService;
    
    @Transactional
    public ProviderTimeOffEntity create(UUID providerId, ProviderTimeOffEntity timeOffData) {
        ProviderProfileEntity provider = providerProfileService.findById(providerId);
        
        if (!timeOffData.getStartAt().isBefore(timeOffData.getEndAt())) {
            throw new InvalidBusinessRuleException("startAt deve ser antes de endAt");
        }
        
        timeOffData.setProvider(provider);
        timeOffData.setCreatedAt(OffsetDateTime.now());
        
        return providerTimeOffRepository.save(timeOffData);
    }
    
    @Transactional(readOnly = true)
    public ProviderTimeOffEntity findById(UUID id) {
        return providerTimeOffRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProviderTimeOff não encontrado com ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<ProviderTimeOffEntity> findByProvider(UUID providerId, Pageable pageable) {
        return providerTimeOffRepository.findAll((root, query, cb) -> 
            cb.equal(root.get("provider").get("id"), providerId), pageable
        );
    }
    
    @Transactional
    public ProviderTimeOffEntity update(UUID id, ProviderTimeOffEntity timeOffUpdate) {
        ProviderTimeOffEntity timeOff = findById(id);
        
        if (timeOffUpdate.getStartAt() != null) {
            timeOff.setStartAt(timeOffUpdate.getStartAt());
        }
        if (timeOffUpdate.getEndAt() != null) {
            timeOff.setEndAt(timeOffUpdate.getEndAt());
        }
        if (timeOffUpdate.getReason() != null) {
            timeOff.setReason(timeOffUpdate.getReason());
        }
        
        return providerTimeOffRepository.save(timeOff);
    }
    
    @Transactional
    public void delete(UUID id) {
        ProviderTimeOffEntity timeOff = findById(id);
        providerTimeOffRepository.delete(timeOff);
    }
}
