package com.agenda.plataform.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.agenda.plataform.dto.provider.ProviderProfileRequest;
import com.agenda.plataform.dto.provider.ProviderProfileResponse;
import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProviderProfileMapper {

    public ProviderProfileEntity toEntity(ProviderProfileRequest request, UserEntity user) {
        return ProviderProfileEntity.builder()
                .user(user)
                .bio(request.getBio())
                .locationText(request.getLocationText())
                .minAdvanceMinutes(request.getMinAdvanceMinutes())
                .cancelWindowMinutes(request.getCancelWindowMinutes())
                .slotStepMinutes(request.getSlotStepMinutes())
                .build();
    }

    public void updateEntity(ProviderProfileEntity entity, ProviderProfileRequest request) {
        if (request.getBio() != null) {
            entity.setBio(request.getBio());
        }
        if (request.getLocationText() != null) {
            entity.setLocationText(request.getLocationText());
        }
        if (request.getMinAdvanceMinutes() != null) {
            entity.setMinAdvanceMinutes(request.getMinAdvanceMinutes());
        }
        if (request.getCancelWindowMinutes() != null) {
            entity.setCancelWindowMinutes(request.getCancelWindowMinutes());
        }
        if (request.getSlotStepMinutes() != null) {
            entity.setSlotStepMinutes(request.getSlotStepMinutes());
        }
    }

    public ProviderProfileResponse toResponse(ProviderProfileEntity entity) {
        return ProviderProfileResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getName())
                .bio(entity.getBio())
                .locationText(entity.getLocationText())
                .minAdvanceMinutes(entity.getMinAdvanceMinutes())
                .cancelWindowMinutes(entity.getCancelWindowMinutes())
                .slotStepMinutes(entity.getSlotStepMinutes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<ProviderProfileResponse> toResponseList(List<ProviderProfileEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
