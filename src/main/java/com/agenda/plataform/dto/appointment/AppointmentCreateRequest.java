package com.agenda.plataform.dto.appointment;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
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
public class AppointmentCreateRequest {
    
    @NotNull(message = "Service ID é obrigatório")
    private UUID serviceId;
    
    @NotNull(message = "Data/hora de início é obrigatória")
    private OffsetDateTime startAt;
    
    @NotNull(message = "Data/hora de fim é obrigatória")
    private OffsetDateTime endAt;
}
