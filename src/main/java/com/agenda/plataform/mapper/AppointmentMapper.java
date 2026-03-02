package com.agenda.plataform.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.agenda.plataform.dto.appointment.AppointmentResponse;
import com.agenda.plataform.entity.AppointmentEntity;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(AppointmentEntity entity) {
        return AppointmentResponse.builder()
                .id(entity.getId())
                .serviceId(entity.getService().getId())
                .serviceName(entity.getService().getName())
                .clientId(entity.getClient().getId())
                .clientName(entity.getClient().getName())
                .providerId(entity.getProvider().getId())
                .providerName(entity.getProvider().getName())
                .status(entity.getStatus())
                .startAt(entity.getStartAt())
                .endAt(entity.getEndAt())
                .cancelReason(entity.getCancelReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<AppointmentResponse> toResponseList(List<AppointmentEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
