package com.agenda.plataform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
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
import com.agenda.plataform.entity.ServiceOfferingEntity;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.ServiceOfferingRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceOfferingService - Testes Unitários")
class ServiceOfferingServiceTest {

    @Mock
    private ServiceOfferingRepository serviceOfferingRepository;

    @Mock
    private ProviderProfileService providerProfileService;

    @InjectMocks
    private ServiceOfferingService serviceOfferingService;

    private ServiceOfferingEntity service;
    private ProviderProfileEntity provider;
    private UUID serviceId;
    private UUID providerId;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();
        providerId = UUID.randomUUID();

        provider = ProviderProfileEntity.builder()
                .id(providerId)
                .bio("Provider profissional")
                .build();

        service = ServiceOfferingEntity.builder()
                .id(serviceId)
                .provider(provider)
                .name("Corte de Cabelo")
                .description("Corte masculino")
                .durationMinutes(30)
                .priceCents(5000)
                .active(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar serviço com sucesso")
    void testCreateServiceSuccess() {
        when(providerProfileService.findById(providerId))
                .thenReturn(provider);
        when(serviceOfferingRepository.save(any(ServiceOfferingEntity.class)))
                .thenReturn(service);

        ServiceOfferingEntity created = serviceOfferingService.create(providerId, service);

        assertNotNull(created);
        assertEquals("Corte de Cabelo", created.getName());
        assertEquals(30, created.getDurationMinutes());
        assertEquals(5000, created.getPriceCents());
        assertTrue(created.getActive());
        verify(providerProfileService, times(1)).findById(providerId);
        verify(serviceOfferingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar criação com duração inválida")
    void testCreateServiceWithInvalidDuration() {
        ServiceOfferingEntity invalidService = ServiceOfferingEntity.builder()
                .name("Serviço Inválido")
                .durationMinutes(0)
                .priceCents(5000)
                .build();

        when(providerProfileService.findById(providerId))
                .thenReturn(provider);

        Exception exception = assertThrows(InvalidBusinessRuleException.class, () ->
                serviceOfferingService.create(providerId, invalidService)
        );

        assertEquals("Duração deve ser maior que 0", exception.getMessage());
        verify(serviceOfferingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar criação com preço negativo")
    void testCreateServiceWithNegativePrice() {
        ServiceOfferingEntity invalidService = ServiceOfferingEntity.builder()
                .name("Serviço Inválido")
                .durationMinutes(30)
                .priceCents(-100)
                .build();

        when(providerProfileService.findById(providerId))
                .thenReturn(provider);

        Exception exception = assertThrows(InvalidBusinessRuleException.class, () ->
                serviceOfferingService.create(providerId, invalidService)
        );

        assertEquals("Preço não pode ser negativo", exception.getMessage());
        verify(serviceOfferingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve encontrar serviço por ID")
    void testFindServiceById() {
        when(serviceOfferingRepository.findById(serviceId))
                .thenReturn(Optional.of(service));

        ServiceOfferingEntity found = serviceOfferingService.findById(serviceId);

        assertNotNull(found);
        assertEquals(serviceId, found.getId());
        assertEquals("Corte de Cabelo", found.getName());
        verify(serviceOfferingRepository, times(1)).findById(serviceId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço não encontrado")
    void testFindServiceByIdNotFound() {
        when(serviceOfferingRepository.findById(serviceId))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                serviceOfferingService.findById(serviceId)
        );

        assertTrue(exception.getMessage().contains("ServiceOffering não encontrado"));
    }

    @Test
    @DisplayName("Deve atualizar serviço")
    void testUpdateService() {
        ServiceOfferingEntity updateData = ServiceOfferingEntity.builder()
                .name("Corte Premium")
                .description("Corte premium com barba")
                .durationMinutes(60)
                .priceCents(10000)
                .build();

        when(serviceOfferingRepository.findById(serviceId))
                .thenReturn(Optional.of(service));
        when(serviceOfferingRepository.save(any(ServiceOfferingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServiceOfferingEntity updated = serviceOfferingService.update(serviceId, updateData);

        assertEquals("Corte Premium", updated.getName());
        assertEquals("Corte premium com barba", updated.getDescription());
        assertEquals(60, updated.getDurationMinutes());
        assertEquals(10000, updated.getPriceCents());
        verify(serviceOfferingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve alternar status ativo do serviço")
    void testToggleServiceActive() {
        when(serviceOfferingRepository.findById(serviceId))
                .thenReturn(Optional.of(service));
        when(serviceOfferingRepository.save(any(ServiceOfferingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertTrue(service.getActive());
        
        serviceOfferingService.toggleActive(serviceId);

        assertFalse(service.getActive());
        verify(serviceOfferingRepository, times(1)).save(any());
    }
}
