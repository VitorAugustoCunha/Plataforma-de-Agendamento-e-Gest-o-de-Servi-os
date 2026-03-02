package com.agenda.plataform.dto.timeoff;

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
public class TimeOffResponse {
    
    private UUID id;
    private UUID providerId;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private String reason;
    private OffsetDateTime createdAt;
}
