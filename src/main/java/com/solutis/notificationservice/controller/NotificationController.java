package com.solutis.notificationservice.controller;

import com.solutis.notificationservice.dto.NotificationResponse;
import com.solutis.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PreAuthorize("hasAnyRole('CLIENT', 'TECHNICIAN', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> findAll(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.findAll(authentication)
        );
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'TECHNICIAN', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> findById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.findById(id, authentication)
        );
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'TECHNICIAN', 'ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> clear(
            Authentication authentication
    ) {

        notificationService.clear(authentication);

        return ResponseEntity.noContent().build();
    }
}