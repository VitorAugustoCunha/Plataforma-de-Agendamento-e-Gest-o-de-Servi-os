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

import com.agenda.plataform.entity.NotificationLogEntity;
import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.NotificationChannel;
import com.agenda.plataform.enums.NotificationStatus;
import com.agenda.plataform.enums.NotificationType;
import com.agenda.plataform.enums.UserRole;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.NotificationLogRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationLogService - Testes Unitários")
class NotificationLogServiceTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationLogService notificationLogService;

    private NotificationLogEntity notification;
    private UserEntity user;
    private UUID notificationId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        notificationId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = UserEntity.builder()
                .id(userId)
                .name("João Silva")
                .email("joao@test.com")
                .role(UserRole.CLIENT)
                .build();

        notification = NotificationLogEntity.builder()
                .id(notificationId)
                .user(user)
                .type(NotificationType.APPOINTMENT_CREATED)
                .channel(NotificationChannel.EMAIL)
                .payload("Seu agendamento foi confirmado")
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar notificação com sucesso")
    void testCreateNotificationSuccess() {
        when(userService.findById(userId))
                .thenReturn(user);
        when(notificationLogRepository.save(any(NotificationLogEntity.class)))
                .thenReturn(notification);

        NotificationLogEntity created = notificationLogService.create(userId, notification);

        assertNotNull(created);
        assertEquals(NotificationStatus.PENDING, created.getStatus());
                assertEquals("Seu agendamento foi confirmado", created.getPayload());
        verify(userService, times(1)).findById(userId);
        verify(notificationLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve encontrar notificação por ID")
    void testFindNotificationById() {
        when(notificationLogRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));

        NotificationLogEntity found = notificationLogService.findById(notificationId);

        assertNotNull(found);
        assertEquals(notificationId, found.getId());
                assertEquals("Seu agendamento foi confirmado", found.getPayload());
        verify(notificationLogRepository, times(1)).findById(notificationId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando notificação não encontrada")
    void testFindNotificationByIdNotFound() {
        when(notificationLogRepository.findById(notificationId))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                notificationLogService.findById(notificationId)
        );

        assertTrue(exception.getMessage().contains("NotificationLog não encontrado"));
    }

    @Test
    @DisplayName("Deve marcar notificação como enviada")
    void testMarkNotificationAsSent() {
        when(notificationLogRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));
        when(notificationLogRepository.save(any(NotificationLogEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationLogEntity marked = notificationLogService.markAsSent(notificationId);

        assertEquals(NotificationStatus.SENT, marked.getStatus());
        assertNotNull(marked.getSentAt());
        verify(notificationLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve marcar notificação como falha")
    void testMarkNotificationAsFailed() {
        String errorMessage = "Erro ao enviar email";
        
        when(notificationLogRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));
        when(notificationLogRepository.save(any(NotificationLogEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationLogEntity marked = notificationLogService.markAsFailed(notificationId, errorMessage);

        assertEquals(NotificationStatus.FAILED, marked.getStatus());
        assertEquals(errorMessage, marked.getErrorMessage());
        verify(notificationLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve processar transição de status corretamente")
    void testNotificationStatusTransition() {
        when(notificationLogRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));
        when(notificationLogRepository.save(any(NotificationLogEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(NotificationStatus.PENDING, notification.getStatus());

        NotificationLogEntity sent = notificationLogService.markAsSent(notificationId);
        assertEquals(NotificationStatus.SENT, sent.getStatus());
        assertNotNull(sent.getSentAt());
    }
}
