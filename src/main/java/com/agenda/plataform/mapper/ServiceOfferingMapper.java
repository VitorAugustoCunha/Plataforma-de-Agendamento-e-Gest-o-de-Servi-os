package com.agenda.plataform.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.agenda.plataform.dto.service.ServiceOfferingRequest;
import com.agenda.plataform.dto.service.ServiceOfferingResponse;
import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.entity.ServiceOfferingEntity;

@Component
public class ServiceOfferingMapper {

    public ServiceOfferingEntity toEntity(ServiceOfferingRequest request, ProviderProfileEntity provider) {
        return ServiceOfferingEntity.builder()
                .provider(provider)
                .name(request.getName())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .priceCents(request.getPriceCents())
                .active(true)
                .build();
    }

    public void updateEntity(ServiceOfferingEntity entity, ServiceOfferingRequest request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getDurationMinutes() != null) {
            entity.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getPriceCents() != null) {
            entity.setPriceCents(request.getPriceCents());
        }
    }

    public ServiceOfferingResponse toResponse(ServiceOfferingEntity entity) {
        return ServiceOfferingResponse.builder()
                .id(entity.getId())
                .providerId(entity.getProvider().getId())
                .providerName(entity.getProvider().getUser().getName())
                .name(entity.getName())
                .description(entity.getDescription())
                .durationMinutes(entity.getDurationMinutes())
                .priceCents(entity.getPriceCents())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<ServiceOfferingResponse> toResponseList(List<ServiceOfferingEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
