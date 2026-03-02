package com.agenda.plataform.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.agenda.plataform.dto.notification.NotificationResponse;
import com.agenda.plataform.entity.NotificationLogEntity;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(NotificationLogEntity entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .type(entity.getType())
                .channel(entity.getChannel())
                .status(entity.getStatus())
                .payload(entity.getPayload())
                .errorMessage(entity.getErrorMessage())
                .sentAt(entity.getSentAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<NotificationResponse> toResponseList(List<NotificationLogEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
