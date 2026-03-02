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

import com.agenda.plataform.dto.service.ServiceOfferingRequest;
import com.agenda.plataform.dto.service.ServiceOfferingResponse;
import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.entity.ServiceOfferingEntity;
import com.agenda.plataform.mapper.ServiceOfferingMapper;
import com.agenda.plataform.service.ProviderProfileService;
import com.agenda.plataform.service.ServiceOfferingService;
import com.agenda.plataform.util.specification.ServiceOfferingSpecifications;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceOfferingController {

    private final ServiceOfferingService serviceOfferingService;
    private final ProviderProfileService providerProfileService;
    private final ServiceOfferingMapper serviceMapper;

    @PostMapping
    public ResponseEntity<ServiceOfferingResponse> create(@Valid @RequestBody ServiceOfferingRequest request) {
        ProviderProfileEntity provider = providerProfileService.findById(request.getProviderId());
        ServiceOfferingEntity service = serviceMapper.toEntity(request, provider);
        ServiceOfferingEntity created = serviceOfferingService.create(request.getProviderId(), service);
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOfferingResponse> getById(@PathVariable UUID id) {
        ServiceOfferingEntity service = serviceOfferingService.findById(id);
        return ResponseEntity.ok(serviceMapper.toResponse(service));
    }

    @GetMapping
    public ResponseEntity<List<ServiceOfferingResponse>> search(
            @RequestParam(required = false) UUID providerId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        
        Page<ServiceOfferingEntity> page = serviceOfferingService.findAll(
                ServiceOfferingSpecifications.byProviderId(providerId)
                        .and(ServiceOfferingSpecifications.byName(name))
                        .and(ServiceOfferingSpecifications.isActive(active)),
                pageable
        );
        
        return ResponseEntity.ok(serviceMapper.toResponseList(page.getContent()));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ServiceOfferingResponse>> getByProviderId(
            @PathVariable UUID providerId,
            Pageable pageable) {
        Page<ServiceOfferingEntity> page = serviceOfferingService.findByProvider(providerId, pageable);
        return ResponseEntity.ok(serviceMapper.toResponseList(page.getContent()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceOfferingResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ServiceOfferingRequest request) {
        
        ServiceOfferingEntity service = serviceOfferingService.findById(id);
        serviceMapper.updateEntity(service, request);
        ServiceOfferingEntity updated = serviceOfferingService.update(id, service);
        return ResponseEntity.ok(serviceMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ServiceOfferingEntity service = serviceOfferingService.findById(id);
        service.setActive(false);
        serviceOfferingService.update(id, service);
        return ResponseEntity.noContent().build();
    }
}
