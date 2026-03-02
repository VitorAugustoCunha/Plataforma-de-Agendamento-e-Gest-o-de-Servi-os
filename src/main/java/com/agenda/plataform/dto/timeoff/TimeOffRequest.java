package com.agenda.plataform.dto.timeoff;

import java.time.OffsetDateTime;

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
public class TimeOffRequest {
    
    @NotNull(message = "Data/hora de início é obrigatória")
    private OffsetDateTime startAt;
    
    @NotNull(message = "Data/hora de fim é obrigatória")
    private OffsetDateTime endAt;
    
    private String reason;
}
