package com.solutis.notificationservice.service;

import com.solutis.notificationservice.dto.NotificationResponse;
import com.solutis.notificationservice.entity.Notification;
import com.solutis.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll(
            Authentication authentication
    ) {

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(Object::toString)
                .orElse("");

        if ("ROLE_CLIENT".equals(role)) {

            UUID customerId = UUID.fromString(
                    authentication.getName()
            );

            return notificationRepository
                    .findByCustomerIdAndActiveTrue(customerId)
                    .stream()
                    .map(NotificationResponse::fromEntity)
                    .toList();
        }

        return notificationRepository
                .findByActiveTrue()
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationResponse findById(
            UUID id,
            Authentication authentication
    ) {

        Notification notification = notificationRepository
                .findById(id)
                .filter(Notification::isActive)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notificação não encontrada: " + id
                        )
                );

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(Object::toString)
                .orElse("");

        if ("ROLE_CLIENT".equals(role)) {

            UUID authenticatedUserId = UUID.fromString(
                    authentication.getName()
            );

            if (!notification.getCustomerId()
                    .equals(authenticatedUserId)) {

                throw new AccessDeniedException(
                        "Você não possui permissão para acessar esta notificação"
                );
            }
        }

        return NotificationResponse.fromEntity(notification);
    }

    @Transactional
    public void clear(
            Authentication authentication
    ) {

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(Object::toString)
                .orElse("");

        if ("ROLE_CLIENT".equals(role)) {

            UUID customerId = UUID.fromString(
                    authentication.getName()
            );

            notificationRepository.deactivateByCustomerId(
                    customerId
            );

            return;
        }

        notificationRepository.deactivateAll();
    }
}