package com.agenda.plataform.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agenda.plataform.entity.AppointmentEntity;
import com.agenda.plataform.entity.ServiceOfferingEntity;
import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.AppointmentStatus;
import com.agenda.plataform.enums.UserRole;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.repository.AppointmentRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService - Testes Unitários")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserService userService;

    @Mock
    private ServiceOfferingService serviceOfferingService;

    @Mock
    private com.agenda.plataform.repository.ProviderTimeOffRepository providerTimeOffRepository;

    @Mock
    private com.agenda.plataform.event.EventPublisher eventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    private UserEntity client;
    private UserEntity provider;
    private ServiceOfferingEntity service;
    private AppointmentEntity appointment;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;

    @BeforeEach
    void setUp() {
        UUID clientId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        client = UserEntity.builder()
                .id(clientId)
                .name("João Cliente")
                .email("cliente@test.com")
                .passwordHash("hash")
                .role(UserRole.CLIENT)
                .active(true)
                .build();

        provider = UserEntity.builder()
                .id(providerId)
                .name("Maria Barbeira")
                .email("provider@test.com")
                .passwordHash("hash")
                .role(UserRole.PROVIDER)
                .active(true)
                .build();

        service = ServiceOfferingEntity.builder()
                .id(serviceId)
                .name("Corte de Cabelo")
                .durationMinutes(30)
                .priceCents(5000)
                .active(true)
                .build();

        startAt = OffsetDateTime.of(2026, 3, 15, 10, 0, 0, 0, ZoneOffset.of("-03:00"));
        endAt = OffsetDateTime.of(2026, 3, 15, 10, 30, 0, 0, ZoneOffset.of("-03:00"));

        appointment = AppointmentEntity.builder()
                .id(UUID.randomUUID())
                .client(client)
                .provider(provider)
                .service(service)
                .startAt(startAt)
                .endAt(endAt)
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    @Test
    @DisplayName("Deve criar agendamento com sucesso")
    void testCreateAppointmentSuccess() {
        service.setProvider(new com.agenda.plataform.entity.ProviderProfileEntity());
        service.getProvider().setUser(provider);
        service.getProvider().setMinAdvanceMinutes(0);
        
        when(userService.findById(client.getId()))
                .thenReturn(client);
        when(serviceOfferingService.findById(service.getId()))
                .thenReturn(service);
        when(appointmentRepository.findOne(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(Optional.empty());
        when(providerTimeOffRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(java.util.Collections.emptyList());
        when(appointmentRepository.save(any(AppointmentEntity.class)))
                .thenReturn(appointment);

        AppointmentEntity created = appointmentService.create(
                client.getId(),
                service.getId(),
                appointment
        );

        assertNotNull(created);
        assertEquals(AppointmentStatus.SCHEDULED, created.getStatus());
        assertEquals(client.getId(), created.getClient().getId());
        assertEquals(service.getId(), created.getService().getId());
        verify(appointmentRepository, times(1)).save(any(AppointmentEntity.class));
    }

    @Test
    @DisplayName("Deve cancelar agendamento com sucesso")
    void testCancelAppointmentSuccess() {
        service.setProvider(new com.agenda.plataform.entity.ProviderProfileEntity());
        service.getProvider().setCancelWindowMinutes(0);
        appointment.setService(service);
        
        when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(AppointmentEntity.class)))
                .thenAnswer(invocation -> {
                    AppointmentEntity arg = invocation.getArgument(0);
                    arg.setStatus(AppointmentStatus.CANCELED);
                    return arg;
                });

        AppointmentEntity canceled = appointmentService.cancel(appointment.getId(), "Mudança de planos");

        assertEquals(AppointmentStatus.CANCELED, canceled.getStatus());
        assertEquals("Mudança de planos", canceled.getCancelReason());
        verify(appointmentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve completar agendamento com sucesso")
    void testCompleteAppointmentSuccess() {
        when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(AppointmentEntity.class)))
                .thenAnswer(invocation -> {
                    AppointmentEntity arg = invocation.getArgument(0);
                    arg.setStatus(AppointmentStatus.COMPLETED);
                    return arg;
                });

        AppointmentEntity completed = appointmentService.complete(appointment.getId());

        assertEquals(AppointmentStatus.COMPLETED, completed.getStatus());
        verify(appointmentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar conclusão de agendamento cancelado")
    void testCompleteAlreadyCanceledAppointment() {
        appointment.setStatus(AppointmentStatus.CANCELED);
        when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        assertThrows(InvalidBusinessRuleException.class, () ->
                appointmentService.complete(appointment.getId())
        );
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve encontrar agendamento por ID")
    void testFindAppointmentById() {
        when(appointmentRepository.findById(appointment.getId()))
                .thenReturn(Optional.of(appointment));

        AppointmentEntity found = appointmentService.findById(appointment.getId());

        assertNotNull(found);
        assertEquals(appointment.getId(), found.getId());
        assertEquals(AppointmentStatus.SCHEDULED, found.getStatus());
        verify(appointmentRepository, times(1)).findById(appointment.getId());
    }
}
