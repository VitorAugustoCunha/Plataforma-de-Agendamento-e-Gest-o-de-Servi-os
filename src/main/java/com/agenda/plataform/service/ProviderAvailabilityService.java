package com.agenda.plataform.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.plataform.entity.ProviderAvailabilityEntity;
import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.ProviderAvailabilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProviderAvailabilityService {
    
    private final ProviderAvailabilityRepository providerAvailabilityRepository;
    private final ProviderProfileService providerProfileService;
    
    @Transactional
    public ProviderAvailabilityEntity create(UUID providerId, ProviderAvailabilityEntity availabilityData) {
        ProviderProfileEntity provider = providerProfileService.findById(providerId);
        
        if (!availabilityData.getStartTime().isBefore(availabilityData.getEndTime())) {
            throw new InvalidBusinessRuleException("startTime deve ser antes de endTime");
        }
        
        availabilityData.setProvider(provider);
        availabilityData.setCreatedAt(OffsetDateTime.now());
        
        return providerAvailabilityRepository.save(availabilityData);
    }
    
    @Transactional(readOnly = true)
    public ProviderAvailabilityEntity findById(UUID id) {
        return providerAvailabilityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProviderAvailability não encontrado com ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<ProviderAvailabilityEntity> findByProvider(UUID providerId, Pageable pageable) {
        return providerAvailabilityRepository.findAll((root, query, cb) -> 
            cb.equal(root.get("provider").get("id"), providerId), pageable
        );
    }
    
    @Transactional
    public ProviderAvailabilityEntity update(UUID id, ProviderAvailabilityEntity availabilityUpdate) {
        ProviderAvailabilityEntity availability = findById(id);
        
        if (availabilityUpdate.getStartTime() != null) {
            availability.setStartTime(availabilityUpdate.getStartTime());
        }
        if (availabilityUpdate.getEndTime() != null) {
            availability.setEndTime(availabilityUpdate.getEndTime());
        }
        if (availabilityUpdate.getDayOfWeek() != null) {
            availability.setDayOfWeek(availabilityUpdate.getDayOfWeek());
        }
        
        return providerAvailabilityRepository.save(availability);
    }
    
    @Transactional
    public void deleteById(UUID id) {
        ProviderAvailabilityEntity availability = findById(id);
        providerAvailabilityRepository.delete(availability);
    }
}
