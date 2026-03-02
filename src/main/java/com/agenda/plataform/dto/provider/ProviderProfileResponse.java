package com.agenda.plataform.dto.provider;

import java.time.OffsetDateTime;
import java.util.UUID;

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
public class ProviderProfileResponse {
    
    private UUID id;
    private UUID userId;
    private String userName;
    private String bio;
    private String locationText;
    private Integer minAdvanceMinutes;
    private Integer cancelWindowMinutes;
    private Integer slotStepMinutes;
    private OffsetDateTime createdAt;
}
