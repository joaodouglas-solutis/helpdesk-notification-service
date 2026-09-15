package com.solutis.notificationservice.service;

import com.solutis.notificationservice.dto.NotificationResponse;
import com.solutis.notificationservice.entity.Notification;
import com.solutis.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll() {

        return notificationRepository.findAll()
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationResponse findById(UUID id) {

        Notification notification = notificationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notificação não encontrada: " + id
                        )
                );

        return NotificationResponse.fromEntity(notification);
    }
}
