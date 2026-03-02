package com.agenda.plataform.dto.service;

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
public class ServiceOfferingResponse {
    
    private UUID id;
    private UUID providerId;
    private String providerName;
    private String name;
    private String description;
    private Integer durationMinutes;
    private Integer priceCents;
    private Boolean active;
    private OffsetDateTime createdAt;
}
