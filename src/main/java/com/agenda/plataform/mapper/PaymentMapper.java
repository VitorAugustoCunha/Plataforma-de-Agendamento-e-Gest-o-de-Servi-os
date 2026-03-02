package com.agenda.plataform.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.agenda.plataform.dto.payment.PaymentResponse;
import com.agenda.plataform.entity.PaymentEntity;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(PaymentEntity entity) {
        return PaymentResponse.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointment().getId())
                .method(entity.getMethod())
                .status(entity.getStatus())
                .amountCents(entity.getAmountCents())
                .externalId(entity.getExternalId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<PaymentResponse> toResponseList(List<PaymentEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
