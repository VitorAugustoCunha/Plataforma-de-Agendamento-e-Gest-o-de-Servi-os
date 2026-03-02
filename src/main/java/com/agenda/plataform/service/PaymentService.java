package com.agenda.plataform.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.plataform.entity.AppointmentEntity;
import com.agenda.plataform.entity.PaymentEntity;
import com.agenda.plataform.enums.PaymentStatus;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final AppointmentService appointmentService;
    
    @Transactional
    public PaymentEntity create(UUID appointmentId, PaymentEntity paymentData) {
        AppointmentEntity appointment = appointmentService.findById(appointmentId);
        
        if (paymentData.getAmountCents() == null || paymentData.getAmountCents() < 0) {
            throw new InvalidBusinessRuleException("Montante inválido");
        }
        
        paymentData.setAppointment(appointment);
        paymentData.setStatus(PaymentStatus.PENDING);
        paymentData.setCreatedAt(OffsetDateTime.now());
        paymentData.setUpdatedAt(OffsetDateTime.now());
        
        return paymentRepository.save(paymentData);
    }
    
    @Transactional(readOnly = true)
    public PaymentEntity findById(UUID id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment não encontrado com ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<PaymentEntity> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }
    
    @Transactional
    public PaymentEntity updateStatus(UUID id, PaymentStatus newStatus) {
        PaymentEntity payment = findById(id);
        payment.setStatus(newStatus);
        payment.setUpdatedAt(OffsetDateTime.now());
        return paymentRepository.save(payment);
    }
    
    @Transactional
    public void deleteById(UUID id) {
        PaymentEntity payment = findById(id);
        paymentRepository.delete(payment);
    }
}
