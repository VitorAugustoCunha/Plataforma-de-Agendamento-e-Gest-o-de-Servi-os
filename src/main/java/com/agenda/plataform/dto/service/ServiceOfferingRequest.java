package com.agenda.plataform.dto.service;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class ServiceOfferingRequest {
    
    @NotNull(message = "ID do provedor é obrigatório")
    private UUID providerId;
    
    @NotBlank(message = "Nome é obrigatório")
    private String name;
    
    private String description;
    
    @NotNull(message = "Duração é obrigatória")
    @Min(value = 1, message = "Duração deve ser maior que 0")
    private Integer durationMinutes;
    
    @NotNull(message = "Preço é obrigatório")
    @Min(value = 0, message = "Preço não pode ser negativo")
    private Integer priceCents;
}
