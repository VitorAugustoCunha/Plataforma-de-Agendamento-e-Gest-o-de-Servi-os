package com.agenda.plataform.dto.notification;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.agenda.plataform.enums.NotificationChannel;
import com.agenda.plataform.enums.NotificationStatus;
import com.agenda.plataform.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    
    private UUID id;
    private UUID userId;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String payload;
    private String errorMessage;
    private OffsetDateTime sentAt;
    private OffsetDateTime createdAt;
}
