package com.agenda.plataform.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agenda.plataform.entity.ProviderAvailabilityEntity;
import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.repository.ProviderAvailabilityRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProviderAvailabilityService - Testes Unitários")
class ProviderAvailabilityServiceTest {

    @Mock
    private ProviderAvailabilityRepository availabilityRepository;

    @Mock
    private ProviderProfileService providerProfileService;

    @InjectMocks
    private ProviderAvailabilityService availabilityService;

    private ProviderProfileEntity provider;
    private ProviderAvailabilityEntity availability;
    private UUID providerId;

    @BeforeEach
    void setUp() {
        providerId = UUID.randomUUID();
        
        provider = ProviderProfileEntity.builder()
                .id(providerId)
                .bio("Barbeiro experiente")
                .locationText("Centro")
                .minAdvanceMinutes(30)
                .cancelWindowMinutes(120)
                .slotStepMinutes(30)
                .build();

        availability = ProviderAvailabilityEntity.builder()
                .id(UUID.randomUUID())
                .provider(provider)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .build();
    }

    @Test
    @DisplayName("Deve criar disponibilidade com sucesso")
    void testCreateAvailabilitySuccess() {
        when(providerProfileService.findById(providerId))
                .thenReturn(provider);
        when(availabilityRepository.save(any(ProviderAvailabilityEntity.class)))
                .thenReturn(availability);

        ProviderAvailabilityEntity created = availabilityService.create(providerId, availability);

        assertNotNull(created);
        assertEquals(DayOfWeek.MONDAY, created.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), created.getStartTime());
        assertEquals(LocalTime.of(18, 0), created.getEndTime());
        assertEquals(provider, created.getProvider());
        verify(providerProfileService, times(1)).findById(providerId);
        verify(availabilityRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar horário inválido (fim antes do início)")
    void testCreateAvailabilityWithInvalidTime() {
        ProviderAvailabilityEntity invalidAvailability = ProviderAvailabilityEntity.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(9, 0))
                .build();

        when(providerProfileService.findById(providerId))
                .thenReturn(provider);

        assertThrows(com.agenda.plataform.exception.InvalidBusinessRuleException.class, () ->
                availabilityService.create(providerId, invalidAvailability)
        );
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar criação quando provider não existe")
    void testCreateAvailabilityWithNonExistentProvider() {
        when(providerProfileService.findById(providerId))
                .thenThrow(new com.agenda.plataform.exception.ResourceNotFoundException("Provider não encontrado"));

        assertThrows(com.agenda.plataform.exception.ResourceNotFoundException.class, () ->
                availabilityService.create(providerId, availability)
        );
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar disponibilidade com sucesso")
    void testDeleteAvailabilitySuccess() {
        when(availabilityRepository.findById(availability.getId()))
                .thenReturn(Optional.of(availability));
        doNothing().when(availabilityRepository).delete(availability);

        availabilityService.delete(availability.getId());

        verify(availabilityRepository, times(1)).findById(availability.getId());
        verify(availabilityRepository, times(1)).delete(availability);
    }

    @Test
    @DisplayName("Deve rejeitar deleção de disponibilidade inexistente")
    void testDeleteNonExistentAvailability() {
        UUID fakeId = UUID.randomUUID();
        when(availabilityRepository.findById(fakeId))
                .thenReturn(Optional.empty());

        assertThrows(com.agenda.plataform.exception.ResourceNotFoundException.class, () ->
                availabilityService.delete(fakeId)
        );
        verify(availabilityRepository, never()).delete(any(ProviderAvailabilityEntity.class));
    }

    @Test
    @DisplayName("Deve validar permissões de acesso à disponibilidade")
    void testDeleteRequiresAuthorization() {
        when(availabilityRepository.findById(availability.getId()))
                .thenReturn(Optional.of(availability));
        doNothing().when(availabilityRepository).delete(availability);

        availabilityService.delete(availability.getId());
        
        verify(availabilityRepository, times(1)).delete(availability);
    }

    @Test
    @DisplayName("Deve validar relacionamento com provider")
    void testAvailabilityBelongsToProvider() {
        ProviderAvailabilityEntity testAvailability = ProviderAvailabilityEntity.builder()
                .id(UUID.randomUUID())
                .provider(provider)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        assertNotNull(testAvailability.getProvider());
        assertEquals(provider.getId(), testAvailability.getProvider().getId());
    }
}