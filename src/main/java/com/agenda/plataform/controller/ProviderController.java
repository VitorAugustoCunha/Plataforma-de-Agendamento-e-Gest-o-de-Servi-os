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
import org.springframework.web.bind.annotation.RestController;

import com.agenda.plataform.dto.availability.AvailabilityRequest;
import com.agenda.plataform.dto.availability.AvailabilityResponse;
import com.agenda.plataform.dto.provider.ProviderProfileRequest;
import com.agenda.plataform.dto.provider.ProviderProfileResponse;
import com.agenda.plataform.dto.timeoff.TimeOffRequest;
import com.agenda.plataform.dto.timeoff.TimeOffResponse;
import com.agenda.plataform.entity.ProviderAvailabilityEntity;
import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.entity.ProviderTimeOffEntity;
import com.agenda.plataform.mapper.AvailabilityMapper;
import com.agenda.plataform.mapper.ProviderProfileMapper;
import com.agenda.plataform.mapper.TimeOffMapper;
import com.agenda.plataform.service.ProviderAvailabilityService;
import com.agenda.plataform.service.ProviderProfileService;
import com.agenda.plataform.service.ProviderTimeOffService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderProfileService providerService;
    private final ProviderAvailabilityService availabilityService;
    private final ProviderTimeOffService timeOffService;
    private final ProviderProfileMapper providerMapper;
    private final AvailabilityMapper availabilityMapper;
    private final TimeOffMapper timeOffMapper;

    // Provider Profile endpoints
    @PostMapping
    public ResponseEntity<ProviderProfileResponse> create(@Valid @RequestBody ProviderProfileRequest request) {
        ProviderProfileEntity profile = providerMapper.toEntity(request, null); // user será setado no service
        ProviderProfileEntity created = providerService.create(request.getUserId(), profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(providerMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderProfileResponse> getById(@PathVariable UUID id) {
        ProviderProfileEntity profile = providerService.findById(id);
        return ResponseEntity.ok(providerMapper.toResponse(profile));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ProviderProfileResponse> getByUserId(@PathVariable UUID userId) {
        ProviderProfileEntity profile = providerService.findByUserId(userId);
        return ResponseEntity.ok(providerMapper.toResponse(profile));
    }

    @GetMapping
    public ResponseEntity<List<ProviderProfileResponse>> getAll(Pageable pageable) {
        Page<ProviderProfileEntity> page = providerService.findAll(pageable);
        return ResponseEntity.ok(providerMapper.toResponseList(page.getContent()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderProfileResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProviderProfileRequest request) {
        
        ProviderProfileEntity profile = providerService.findById(id);
        providerMapper.updateEntity(profile, request);
        ProviderProfileEntity updated = providerService.update(id, profile);
        return ResponseEntity.ok(providerMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        // Implementar lógica de deleção se necessário
        // Por enquanto, retorna 501 Not Implemented
        return ResponseEntity.status(501).build();
    }

    // Availability endpoints
    @PostMapping("/{providerId}/availability")
    public ResponseEntity<AvailabilityResponse> createAvailability(
            @PathVariable UUID providerId,
            @Valid @RequestBody AvailabilityRequest request) {
        
        ProviderProfileEntity provider = providerService.findById(providerId);
        ProviderAvailabilityEntity availability = availabilityMapper.toEntity(request, provider);
        ProviderAvailabilityEntity created = availabilityService.create(providerId, availability);
        return ResponseEntity.status(HttpStatus.CREATED).body(availabilityMapper.toResponse(created));
    }

    @GetMapping("/{providerId}/availability")
    public ResponseEntity<List<AvailabilityResponse>> getAvailability(
            @PathVariable UUID providerId,
            Pageable pageable) {
        Page<ProviderAvailabilityEntity> page = availabilityService.findByProvider(providerId, pageable);
        return ResponseEntity.ok(availabilityMapper.toResponseList(page.getContent()));
    }

    @PutMapping("/{providerId}/availability/{availabilityId}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(
            @PathVariable UUID providerId,
            @PathVariable UUID availabilityId,
            @Valid @RequestBody AvailabilityRequest request) {
        
        ProviderAvailabilityEntity availability = availabilityService.findById(availabilityId);
        availabilityMapper.updateEntity(availability, request);
        ProviderAvailabilityEntity updated = availabilityService.update(availabilityId, availability);
        return ResponseEntity.ok(availabilityMapper.toResponse(updated));
    }

    @DeleteMapping("/{providerId}/availability/{availabilityId}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable UUID providerId,
            @PathVariable UUID availabilityId) {
        // Implementar lógica de deleção se necessário
        return ResponseEntity.status(501).build();
    }

    // TimeOff endpoints
    @PostMapping("/{providerId}/timeoff")
    public ResponseEntity<TimeOffResponse> createTimeOff(
            @PathVariable UUID providerId,
            @Valid @RequestBody TimeOffRequest request) {
        
        ProviderProfileEntity provider = providerService.findById(providerId);
        ProviderTimeOffEntity timeOff = timeOffMapper.toEntity(request, provider);
        ProviderTimeOffEntity created = timeOffService.create(providerId, timeOff);
        return ResponseEntity.status(HttpStatus.CREATED).body(timeOffMapper.toResponse(created));
    }

    @GetMapping("/{providerId}/timeoff")
    public ResponseEntity<List<TimeOffResponse>> getTimeOff(
            @PathVariable UUID providerId,
            Pageable pageable) {
        Page<ProviderTimeOffEntity> page = timeOffService.findByProvider(providerId, pageable);
        return ResponseEntity.ok(timeOffMapper.toResponseList(page.getContent()));
    }

    @PutMapping("/{providerId}/timeoff/{timeOffId}")
    public ResponseEntity<TimeOffResponse> updateTimeOff(
            @PathVariable UUID providerId,
            @PathVariable UUID timeOffId,
            @Valid @RequestBody TimeOffRequest request) {
        
        ProviderTimeOffEntity timeOff = timeOffService.findById(timeOffId);
        timeOffMapper.updateEntity(timeOff, request);
        ProviderTimeOffEntity updated = timeOffService.update(timeOffId, timeOff);
        return ResponseEntity.ok(timeOffMapper.toResponse(updated));
    }

    @DeleteMapping("/{providerId}/timeoff/{timeOffId}")
    public ResponseEntity<Void> deleteTimeOff(
            @PathVariable UUID providerId,
            @PathVariable UUID timeOffId) {
        // Implementar lógica de deleção se necessário
        return ResponseEntity.status(501).build();
    }
}
