package com.agenda.plataform.dto.availability;

import java.time.DayOfWeek;
import java.time.LocalTime;

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
public class AvailabilityRequest {
    
    @NotNull(message = "Dia da semana é obrigatório")
    private DayOfWeek dayOfWeek;
    
    @NotNull(message = "Hora de início é obrigatória")
    private LocalTime startTime;
    
    @NotNull(message = "Hora de fim é obrigatória")
    private LocalTime endTime;
}
