package com.agenda.plataform.event;

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
public class DomainEvent {
    
    private UUID aggregateId;
    private String eventType;
    private OffsetDateTime occurredAt;
    private Object eventData;
    
    public static DomainEvent create(String eventType, UUID aggregateId, Object eventData) {
        return DomainEvent.builder()
                .eventType(eventType)
                .aggregateId(aggregateId)
                .eventData(eventData)
                .occurredAt(OffsetDateTime.now())
                .build();
    }
}
