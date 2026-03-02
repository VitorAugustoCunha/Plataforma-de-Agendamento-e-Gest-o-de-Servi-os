package com.agenda.plataform.dto.payment;

import java.util.UUID;

import com.agenda.plataform.enums.PaymentMethod;

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
public class PaymentRequest {
    
    @NotNull(message = "Agendamento é obrigatório")
    private UUID appointmentId;
    
    @NotNull(message = "Método de pagamento é obrigatório")
    private PaymentMethod method;
    
    @NotNull(message = "Valor é obrigatório")
    @Min(value = 0, message = "Valor não pode ser negativo")
    private Integer amountCents;
    
    private String externalId;
}
