package com.agenda.plataform.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.agenda.plataform.event.AppointmentCanceledEvent;
import com.agenda.plataform.event.AppointmentScheduledEvent;
import com.agenda.plataform.event.NotificationSentEvent;
import com.agenda.plataform.event.PaymentProcessedEvent;
import com.agenda.plataform.event.UserCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DomainEventListener {
    
    @Autowired(required = false)
    private EmailService emailService;
    
    @EventListener
    @Async
    public void onUserCreated(UserCreatedEvent event) {
        log.info("Usuário criado: {} ({})", event.getName(), event.getEmail());
        if (emailService != null) {
            emailService.sendWelcomeEmail(event.getEmail(), event.getName());
        }
    }
    
    @EventListener
    @Async
    public void onAppointmentScheduled(AppointmentScheduledEvent event) {
        log.info("Agendamento criado: {} - Cliente: {}, Provedor: {}", 
            event.getAppointmentId(), event.getClientId(), event.getProviderId());
        if (emailService != null) {
            emailService.sendAppointmentConfirmation(event.getAppointmentId(), event.getClientId());
        }
    }
    
    @EventListener
    @Async
    public void onAppointmentCanceled(AppointmentCanceledEvent event) {
        log.info("Agendamento cancelado: {} - Razão: {}", 
            event.getAppointmentId(), event.getCancelReason());
        if (emailService != null) {
            emailService.sendAppointmentCancellation(event.getAppointmentId(), event.getClientId());
        }
    }
    
    @EventListener
    @Async
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        log.info("Pagamento processado: {} - Status: {} - Valor: {}", 
            event.getPaymentId(), event.getStatus(), event.getAmountCents());
    }
    
    @EventListener
    @Async
    public void onNotificationSent(NotificationSentEvent event) {
        log.info("Notificação enviada: {} - Tipo: {} - Canal: {} - Status: {}", 
            event.getNotificationId(), event.getType(), event.getChannel(), event.getStatus());
    }
}
