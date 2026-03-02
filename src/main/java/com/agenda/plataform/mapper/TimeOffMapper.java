package com.agenda.plataform.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.agenda.plataform.dto.timeoff.TimeOffRequest;
import com.agenda.plataform.dto.timeoff.TimeOffResponse;
import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.entity.ProviderTimeOffEntity;

@Component
public class TimeOffMapper {

    public ProviderTimeOffEntity toEntity(TimeOffRequest request, ProviderProfileEntity provider) {
        return ProviderTimeOffEntity.builder()
                .provider(provider)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .reason(request.getReason())
                .build();
    }

    public void updateEntity(ProviderTimeOffEntity entity, TimeOffRequest request) {
        if (request.getStartAt() != null) {
            entity.setStartAt(request.getStartAt());
        }
        if (request.getEndAt() != null) {
            entity.setEndAt(request.getEndAt());
        }
        if (request.getReason() != null) {
            entity.setReason(request.getReason());
        }
    }

    public TimeOffResponse toResponse(ProviderTimeOffEntity entity) {
        return TimeOffResponse.builder()
                .id(entity.getId())
                .providerId(entity.getProvider().getId())
                .startAt(entity.getStartAt())
                .endAt(entity.getEndAt())
                .reason(entity.getReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<TimeOffResponse> toResponseList(List<ProviderTimeOffEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
