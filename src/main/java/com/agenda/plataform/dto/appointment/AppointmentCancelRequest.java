package com.agenda.plataform.dto.appointment;

import jakarta.validation.constraints.NotBlank;
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
public class AppointmentCancelRequest {
    
    @NotBlank(message = "Motivo de cancelamento é obrigatório")
    private String cancelReason;
}
