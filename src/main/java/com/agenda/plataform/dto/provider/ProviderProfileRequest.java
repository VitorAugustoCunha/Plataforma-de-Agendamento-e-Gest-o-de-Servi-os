package com.agenda.plataform.dto.provider;

import java.util.UUID;

import jakarta.validation.constraints.Min;
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
public class ProviderProfileRequest {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private UUID userId;
    
    private String bio;
    private String locationText;
    
    @Min(value = 0, message = "Antecedência mínima deve ser >= 0")
    private Integer minAdvanceMinutes;
    
    @Min(value = 0, message = "Janela de cancelamento deve ser >= 0")
    private Integer cancelWindowMinutes;
    
    @Min(value = 15, message = "Intervalo de slots deve ser >= 15")
    private Integer slotStepMinutes;
}
