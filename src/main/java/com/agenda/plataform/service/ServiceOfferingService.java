package com.agenda.plataform.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.entity.ServiceOfferingEntity;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.ServiceOfferingRepository;
import com.agenda.plataform.util.specification.ServiceOfferingSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceOfferingService {
    
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ProviderProfileService providerProfileService;
    
    @Transactional
    public ServiceOfferingEntity create(UUID providerId, ServiceOfferingEntity serviceData) {
        ProviderProfileEntity provider = providerProfileService.findById(providerId);
        
        if (serviceData.getDurationMinutes() == null || serviceData.getDurationMinutes() <= 0) {
            throw new InvalidBusinessRuleException("Duração deve ser maior que 0");
        }
        
        if (serviceData.getPriceCents() == null || serviceData.getPriceCents() < 0) {
            throw new InvalidBusinessRuleException("Preço não pode ser negativo");
        }
        
        serviceData.setProvider(provider);
        serviceData.setActive(true);
        serviceData.setCreatedAt(OffsetDateTime.now());
        serviceData.setUpdatedAt(OffsetDateTime.now());
        return serviceOfferingRepository.save(serviceData);
    }
    
    @Transactional(readOnly = true)
    public ServiceOfferingEntity findById(UUID id) {
        return serviceOfferingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ServiceOffering não encontrado com ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<ServiceOfferingEntity> findByProvider(UUID providerId, Pageable pageable) {
        Specification<ServiceOfferingEntity> spec = ServiceOfferingSpecifications.byProviderId(providerId)
            .and(ServiceOfferingSpecifications.isActive(true));
        return serviceOfferingRepository.findAll(spec, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<ServiceOfferingEntity> findAll(Pageable pageable) {
        return serviceOfferingRepository.findAll(pageable);
    }
    
    @Transactional
    public ServiceOfferingEntity update(UUID id, ServiceOfferingEntity serviceUpdate) {
        ServiceOfferingEntity service = findById(id);
        
        if (serviceUpdate.getName() != null) {
            service.setName(serviceUpdate.getName());
        }
        if (serviceUpdate.getDescription() != null) {
            service.setDescription(serviceUpdate.getDescription());
        }
        if (serviceUpdate.getDurationMinutes() != null && serviceUpdate.getDurationMinutes() > 0) {
            service.setDurationMinutes(serviceUpdate.getDurationMinutes());
        }
        if (serviceUpdate.getPriceCents() != null && serviceUpdate.getPriceCents() >= 0) {
            service.setPriceCents(serviceUpdate.getPriceCents());
        }
        
        service.setUpdatedAt(OffsetDateTime.now());
        return serviceOfferingRepository.save(service);
    }
    
    @Transactional
    public void toggleActive(UUID id) {
        ServiceOfferingEntity service = findById(id);
        service.setActive(!service.getActive());
        service.setUpdatedAt(OffsetDateTime.now());
        serviceOfferingRepository.save(service);
    }
}
