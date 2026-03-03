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

import com.agenda.plataform.entity.AppointmentEntity;
import com.agenda.plataform.entity.PaymentEntity;
import com.agenda.plataform.enums.PaymentStatus;
import com.agenda.plataform.event.EventPublisher;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService - Testes Unitários")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentEntity payment;
    private AppointmentEntity appointment;
    private UUID paymentId;
    private UUID appointmentId;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        appointment = AppointmentEntity.builder()
                .id(appointmentId)
                .build();

        payment = PaymentEntity.builder()
                .id(paymentId)
                .appointment(appointment)
                .amountCents(5000)
                .status(PaymentStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar pagamento com sucesso")
    void testCreatePaymentSuccess() {
        when(appointmentService.findById(appointmentId))
                .thenReturn(appointment);
        when(paymentRepository.save(any(PaymentEntity.class)))
                .thenReturn(payment);

        PaymentEntity created = paymentService.create(appointmentId, payment);

        assertNotNull(created);
        assertEquals(PaymentStatus.PENDING, created.getStatus());
        assertEquals(5000, created.getAmountCents());
        verify(appointmentService, times(1)).findById(appointmentId);
        verify(paymentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar criação com montante inválido")
    void testCreatePaymentWithInvalidAmount() {
        PaymentEntity invalidPayment = PaymentEntity.builder()
                .amountCents(-100)
                .build();

        when(appointmentService.findById(appointmentId))
                .thenReturn(appointment);

        Exception exception = assertThrows(InvalidBusinessRuleException.class, () ->
                paymentService.create(appointmentId, invalidPayment)
        );

        assertEquals("Montante inválido", exception.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve encontrar pagamento por ID")
    void testFindPaymentById() {
        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        PaymentEntity found = paymentService.findById(paymentId);

        assertNotNull(found);
        assertEquals(paymentId, found.getId());
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento não encontrado")
    void testFindPaymentByIdNotFound() {
        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                paymentService.findById(paymentId)
        );

        assertTrue(exception.getMessage().contains("Payment não encontrado"));
    }

    @Test
    @DisplayName("Deve atualizar status do pagamento")
    void testUpdatePaymentStatus() {
        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(PaymentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentEntity updated = paymentService.updateStatus(paymentId, PaymentStatus.PAID);

        assertEquals(PaymentStatus.PAID, updated.getStatus());
        verify(paymentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve deletar pagamento por ID")
    void testDeletePaymentById() {
        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));
        doNothing().when(paymentRepository).delete(payment);

        paymentService.deleteById(paymentId);

        verify(paymentRepository, times(1)).delete(payment);
    }
}
