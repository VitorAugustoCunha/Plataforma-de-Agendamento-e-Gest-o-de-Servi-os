package com.agenda.plataform.event;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.agenda.plataform.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentProcessedEvent {
    private UUID paymentId;
    private UUID appointmentId;
    private PaymentStatus status;
    private Integer amountCents;
    private String externalId;
    private OffsetDateTime processedAt;
}
