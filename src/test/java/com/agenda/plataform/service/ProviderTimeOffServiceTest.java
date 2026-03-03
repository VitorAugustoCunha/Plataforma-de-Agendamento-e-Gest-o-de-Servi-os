package com.agenda.plataform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.entity.ProviderTimeOffEntity;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.ProviderTimeOffRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProviderTimeOffService - Testes Unitários")
class ProviderTimeOffServiceTest {

    @Mock
    private ProviderTimeOffRepository providerTimeOffRepository;

    @Mock
    private ProviderProfileService providerProfileService;

    @InjectMocks
    private ProviderTimeOffService providerTimeOffService;

    private ProviderTimeOffEntity timeOff;
    private ProviderProfileEntity provider;
    private UUID timeOffId;
    private UUID providerId;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;

    @BeforeEach
    void setUp() {
        timeOffId = UUID.randomUUID();
        providerId = UUID.randomUUID();

        provider = ProviderProfileEntity.builder()
                .id(providerId)
                .bio("Provider profissional")
                .build();

        startAt = OffsetDateTime.of(2026, 3, 10, 9, 0, 0, 0, ZoneOffset.of("-03:00"));
        endAt = OffsetDateTime.of(2026, 3, 10, 18, 0, 0, 0, ZoneOffset.of("-03:00"));

        timeOff = ProviderTimeOffEntity.builder()
                .id(timeOffId)
                .provider(provider)
                .startAt(startAt)
                .endAt(endAt)
                .reason("Férias")
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar período de indisponibilidade com sucesso")
    void testCreateTimeOffSuccess() {
        when(providerProfileService.findById(providerId))
                .thenReturn(provider);
        when(providerTimeOffRepository.save(any(ProviderTimeOffEntity.class)))
                .thenReturn(timeOff);

        ProviderTimeOffEntity created = providerTimeOffService.create(providerId, timeOff);

        assertNotNull(created);
        assertEquals("Férias", created.getReason());
        assertEquals(startAt, created.getStartAt());
        assertEquals(endAt, created.getEndAt());
        verify(providerProfileService, times(1)).findById(providerId);
        verify(providerTimeOffRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar criação com datas inválidas")
    void testCreateTimeOffWithInvalidDates() {
        ProviderTimeOffEntity invalidTimeOff = ProviderTimeOffEntity.builder()
                .startAt(endAt)
                .endAt(startAt)
                .reason("Inválido")
                .build();

        when(providerProfileService.findById(providerId))
                .thenReturn(provider);

        Exception exception = assertThrows(InvalidBusinessRuleException.class, () ->
                providerTimeOffService.create(providerId, invalidTimeOff)
        );

        assertEquals("startAt deve ser antes de endAt", exception.getMessage());
        verify(providerTimeOffRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve encontrar período por ID")
    void testFindTimeOffById() {
        when(providerTimeOffRepository.findById(timeOffId))
                .thenReturn(Optional.of(timeOff));

        ProviderTimeOffEntity found = providerTimeOffService.findById(timeOffId);

        assertNotNull(found);
        assertEquals(timeOffId, found.getId());
        assertEquals("Férias", found.getReason());
        verify(providerTimeOffRepository, times(1)).findById(timeOffId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando período não encontrado")
    void testFindTimeOffByIdNotFound() {
        when(providerTimeOffRepository.findById(timeOffId))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                providerTimeOffService.findById(timeOffId)
        );

        assertTrue(exception.getMessage().contains("ProviderTimeOff não encontrado"));
    }

    @Test
    @DisplayName("Deve atualizar período de indisponibilidade")
    void testUpdateTimeOff() {
        OffsetDateTime newEnd = OffsetDateTime.of(2026, 3, 12, 18, 0, 0, 0, ZoneOffset.of("-03:00"));
        ProviderTimeOffEntity updateData = ProviderTimeOffEntity.builder()
                .endAt(newEnd)
                .reason("Férias estendidas")
                .build();

        when(providerTimeOffRepository.findById(timeOffId))
                .thenReturn(Optional.of(timeOff));
        when(providerTimeOffRepository.save(any(ProviderTimeOffEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProviderTimeOffEntity updated = providerTimeOffService.update(timeOffId, updateData);

        assertEquals("Férias estendidas", updated.getReason());
        assertEquals(newEnd, updated.getEndAt());
        verify(providerTimeOffRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve deletar período de indisponibilidade")
    void testDeleteTimeOff() {
        when(providerTimeOffRepository.findById(timeOffId))
                .thenReturn(Optional.of(timeOff));
        doNothing().when(providerTimeOffRepository).delete(timeOff);

        providerTimeOffService.delete(timeOffId);

        verify(providerTimeOffRepository, times(1)).delete(timeOff);
    }
}
