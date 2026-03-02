package com.agenda.plataform.event;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentScheduledEvent {
    private UUID appointmentId;
    private UUID clientId;
    private UUID providerId;
    private UUID serviceId;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private OffsetDateTime scheduledAt;
}
