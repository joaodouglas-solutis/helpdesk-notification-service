package com.solutis.notificationservice.controller;

import com.solutis.notificationservice.dto.NotificationResponse;
import com.solutis.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> findAll() {
        return ResponseEntity.ok(
                notificationService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                notificationService.findById(id)
        );
    }
}