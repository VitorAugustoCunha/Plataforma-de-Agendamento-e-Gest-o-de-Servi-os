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
import org.springframework.web.bind.annotation.RestController;

import com.agenda.plataform.dto.payment.PaymentRequest;
import com.agenda.plataform.dto.payment.PaymentResponse;
import com.agenda.plataform.entity.AppointmentEntity;
import com.agenda.plataform.entity.PaymentEntity;
import com.agenda.plataform.enums.PaymentStatus;
import com.agenda.plataform.mapper.PaymentMapper;
import com.agenda.plataform.service.AppointmentService;
import com.agenda.plataform.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AppointmentService appointmentService;
    private final PaymentMapper paymentMapper;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request) {
        AppointmentEntity appointment = appointmentService.findById(request.getAppointmentId());
        
        PaymentEntity payment = PaymentEntity.builder()
                .appointment(appointment)
                .method(request.getMethod())
                .amountCents(request.getAmountCents())
                .externalId(request.getExternalId())
                .status(PaymentStatus.PENDING)
                .build();
        
        PaymentEntity created = paymentService.create(request.getAppointmentId(), payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable UUID id) {
        PaymentEntity payment = paymentService.findById(id);
        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<PaymentResponse>> getByAppointmentId(
            @PathVariable UUID appointmentId,
            Pageable pageable) {
        Page<PaymentEntity> page = paymentService.findByAppointment(appointmentId, pageable);
        return ResponseEntity.ok(paymentMapper.toResponseList(page.getContent()));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<PaymentResponse> approve(@PathVariable UUID id) {
        PaymentEntity updated = paymentService.updateStatus(id, PaymentStatus.PAID);
        return ResponseEntity.ok(paymentMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<PaymentResponse> reject(@PathVariable UUID id) {
        PaymentEntity updated = paymentService.updateStatus(id, PaymentStatus.FAILED);
        return ResponseEntity.ok(paymentMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refund(@PathVariable UUID id) {
        PaymentEntity updated = paymentService.updateStatus(id, PaymentStatus.REFUNDED);
        return ResponseEntity.ok(paymentMapper.toResponse(updated));
    }
}
