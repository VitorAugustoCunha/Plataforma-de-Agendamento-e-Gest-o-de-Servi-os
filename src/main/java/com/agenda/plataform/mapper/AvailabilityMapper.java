package com.agenda.plataform.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.agenda.plataform.dto.availability.AvailabilityRequest;
import com.agenda.plataform.dto.availability.AvailabilityResponse;
import com.agenda.plataform.entity.ProviderAvailabilityEntity;
import com.agenda.plataform.entity.ProviderProfileEntity;

@Component
public class AvailabilityMapper {

    public ProviderAvailabilityEntity toEntity(AvailabilityRequest request, ProviderProfileEntity provider) {
        return ProviderAvailabilityEntity.builder()
                .provider(provider)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
    }

    public void updateEntity(ProviderAvailabilityEntity entity, AvailabilityRequest request) {
        if (request.getDayOfWeek() != null) {
            entity.setDayOfWeek(request.getDayOfWeek());
        }
        if (request.getStartTime() != null) {
            entity.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            entity.setEndTime(request.getEndTime());
        }
    }

    public AvailabilityResponse toResponse(ProviderAvailabilityEntity entity) {
        return AvailabilityResponse.builder()
                .id(entity.getId())
                .providerId(entity.getProvider().getId())
                .dayOfWeek(entity.getDayOfWeek())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<AvailabilityResponse> toResponseList(List<ProviderAvailabilityEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
