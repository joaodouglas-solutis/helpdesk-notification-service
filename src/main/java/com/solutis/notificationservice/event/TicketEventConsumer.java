package com.solutis.notificationservice.event;

import com.solutis.notificationservice.entity.Notification;
import com.solutis.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketEventConsumer {

    private final NotificationRepository notificationRepository;

    @RabbitListener(
            queues = "ticket.created.queue"
    )
    public void consumeCreated(TicketCreatedEvent event) {

        Notification notification = Notification.builder()
                .ticketId(event.ticketId())
                .customerId(event.customerId())
                .message(
                        "Chamado criado: " + event.title()
                )
                .eventType("TicketCreated")
                .build();

        notificationRepository.save(notification);
    }

    @RabbitListener(
            queues = "ticket.assigned.queue"
    )
    public void consumeAssigned(TicketAssignedEvent event) {

        Notification notification = Notification.builder()
                .ticketId(event.ticketId())
                .customerId(event.customerId())
                .message(
                        "Chamado atribuído a um técnico."
                )
                .eventType("TicketAssigned")
                .build();

        notificationRepository.save(notification);
    }

    @RabbitListener(
            queues = "ticket.status-changed.queue"
    )
    public void consumeStatusChanged(
            TicketStatusChangedEvent event
    ) {

        Notification notification = Notification.builder()
                .ticketId(event.ticketId())
                .customerId(event.customerId())
                .message(
                        "Status alterado de "
                                + event.previousStatus()
                                + " para "
                                + event.newStatus()
                                + "."
                )
                .eventType("TicketStatusChanged")
                .build();

        notificationRepository.save(notification);
    }
}