package com.agenda.plataform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agenda.plataform.dto.appointment.AppointmentCancelRequest;
import com.agenda.plataform.dto.appointment.AppointmentCreateRequest;
import com.agenda.plataform.dto.appointment.AppointmentResponse;
import com.agenda.plataform.entity.AppointmentEntity;
import com.agenda.plataform.entity.ServiceOfferingEntity;
import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.AppointmentStatus;
import com.agenda.plataform.mapper.AppointmentMapper;
import com.agenda.plataform.service.AppointmentService;
import com.agenda.plataform.service.ServiceOfferingService;
import com.agenda.plataform.service.UserService;
import com.agenda.plataform.util.specification.AppointmentSpecifications;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ServiceOfferingService serviceOfferingService;
    private final UserService userService;
    private final AppointmentMapper appointmentMapper;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @Valid @RequestBody AppointmentCreateRequest request,
            @RequestParam UUID clientId) {
        
        ServiceOfferingEntity service = serviceOfferingService.findById(request.getServiceId());
        UserEntity client = userService.findById(clientId);
        UserEntity provider = service.getProvider().getUser();

        AppointmentEntity appointment = AppointmentEntity.builder()
                .service(service)
                .client(client)
                .provider(provider)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        AppointmentEntity created = appointmentService.create(clientId, request.getServiceId(), appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable UUID id) {
        AppointmentEntity appointment = appointmentService.findById(id);
        return ResponseEntity.ok(appointmentMapper.toResponse(appointment));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> search(
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) UUID providerId,
            @RequestParam(required = false) AppointmentStatus status,
            Pageable pageable) {
        
        Page<AppointmentEntity> page = appointmentService.findAll(
                AppointmentSpecifications.byClientId(clientId)
                        .and(AppointmentSpecifications.byProviderId(providerId))
                        .and(AppointmentSpecifications.byStatus(status)),
                pageable
        );
        
        return ResponseEntity.ok(appointmentMapper.toResponseList(page.getContent()));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AppointmentResponse>> getByClientId(
            @PathVariable UUID clientId,
            Pageable pageable) {
        Page<AppointmentEntity> page = appointmentService.findByClient(clientId, pageable);
        return ResponseEntity.ok(appointmentMapper.toResponseList(page.getContent()));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<AppointmentResponse>> getByProviderId(
            @PathVariable UUID providerId,
            Pageable pageable) {
        Page<AppointmentEntity> page = appointmentService.findByProvider(providerId, pageable);
        return ResponseEntity.ok(appointmentMapper.toResponseList(page.getContent()));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable UUID id,
            @Valid @RequestBody AppointmentCancelRequest request) {
        
        AppointmentEntity canceled = appointmentService.cancel(id, request.getCancelReason());
        return ResponseEntity.ok(appointmentMapper.toResponse(canceled));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable UUID id) {
        AppointmentEntity completed = appointmentService.complete(id);
        return ResponseEntity.ok(appointmentMapper.toResponse(completed));
    }
}
