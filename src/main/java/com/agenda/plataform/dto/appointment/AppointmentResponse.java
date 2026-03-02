package com.agenda.plataform.dto.appointment;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.agenda.plataform.enums.AppointmentStatus;

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
public class AppointmentResponse {
    
    private UUID id;
    private UUID clientId;
    private String clientName;
    private UUID providerId;
    private String providerName;
    private UUID serviceId;
    private String serviceName;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private AppointmentStatus status;
    private String cancelReason;
    private OffsetDateTime canceledAt;
    private OffsetDateTime createdAt;
}
