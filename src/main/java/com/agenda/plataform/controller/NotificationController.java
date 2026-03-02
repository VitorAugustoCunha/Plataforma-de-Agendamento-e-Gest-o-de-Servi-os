package com.agenda.plataform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agenda.plataform.dto.notification.NotificationResponse;
import com.agenda.plataform.entity.NotificationLogEntity;
import com.agenda.plataform.enums.NotificationStatus;
import com.agenda.plataform.mapper.NotificationMapper;
import com.agenda.plataform.service.NotificationLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationLogService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(@PathVariable UUID id) {
        NotificationLogEntity notification = notificationService.findById(id);
        return ResponseEntity.ok(notificationMapper.toResponse(notification));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getByUserId(
            @PathVariable UUID userId,
            @RequestParam(required = false) NotificationStatus status,
            Pageable pageable) {
        
        Page<NotificationLogEntity> page;
        if (status != null) {
            page = notificationService.findByUserAndStatus(userId, status, pageable);
        } else {
            page = notificationService.findByUser(userId, pageable);
        }
        
        return ResponseEntity.ok(notificationMapper.toResponseList(page.getContent()));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll(Pageable pageable) {
        Page<NotificationLogEntity> page = notificationService.findAll(pageable);
        return ResponseEntity.ok(notificationMapper.toResponseList(page.getContent()));
    }
}
