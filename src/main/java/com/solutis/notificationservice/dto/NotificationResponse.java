package com.solutis.notificationservice.dto;

import com.solutis.notificationservice.entity.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID ticketId,
        UUID customerId,
        String message,
        String eventType,
        LocalDateTime createdAt
) {

    public static NotificationResponse fromEntity(
            Notification notification
    ) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTicketId(),
                notification.getCustomerId(),
                notification.getMessage(),
                notification.getEventType(),
                notification.getCreatedAt()
        );
    }
}