package com.agenda.plataform.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventPublisher {
    
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    
    public void publishUserCreated(UserCreatedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    public void publishAppointmentScheduled(AppointmentScheduledEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    public void publishAppointmentCanceled(AppointmentCanceledEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    public void publishPaymentProcessed(PaymentProcessedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    public void publishNotificationSent(NotificationSentEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
