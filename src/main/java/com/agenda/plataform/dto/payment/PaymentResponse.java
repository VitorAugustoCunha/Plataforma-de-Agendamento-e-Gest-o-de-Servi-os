package com.agenda.plataform.dto.payment;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.agenda.plataform.enums.PaymentMethod;
import com.agenda.plataform.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    
    private UUID id;
    private UUID appointmentId;
    private PaymentMethod method;
    private PaymentStatus status;
    private Integer amountCents;
    private String externalId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
