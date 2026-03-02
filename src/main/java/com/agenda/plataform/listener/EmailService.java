package com.agenda.plataform.listener;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailService {
    
    public void sendWelcomeEmail(String email, String name) {
        System.out.println("Enviando email de boas-vindas para: " + email);
    }
    
    public void sendAppointmentConfirmation(UUID appointmentId, UUID clientId) {
        System.out.println("Enviando confirmação de agendamento para cliente: " + clientId);
    }
    
    public void sendAppointmentCancellation(UUID appointmentId, UUID clientId) {
        System.out.println("Enviando notificação de cancelamento para cliente: " + clientId);
    }
}
