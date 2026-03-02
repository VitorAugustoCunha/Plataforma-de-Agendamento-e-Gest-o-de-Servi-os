package com.agenda.plataform.event;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.agenda.plataform.enums.NotificationChannel;
import com.agenda.plataform.enums.NotificationStatus;
import com.agenda.plataform.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationSentEvent {
    private UUID notificationId;
    private UUID userId;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String recipient;
    private String payload;
    private OffsetDateTime sentAt;
}
