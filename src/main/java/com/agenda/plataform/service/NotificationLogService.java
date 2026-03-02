package com.agenda.plataform.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.plataform.entity.NotificationLogEntity;
import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.NotificationStatus;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.NotificationLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationLogService {
    
    private final NotificationLogRepository notificationLogRepository;
    private final UserService userService;
    
    @Transactional
    public NotificationLogEntity create(UUID userId, NotificationLogEntity notificationData) {
        UserEntity user = userService.findById(userId);
        
        notificationData.setUser(user);
        notificationData.setStatus(NotificationStatus.PENDING);
        notificationData.setCreatedAt(OffsetDateTime.now());
        
        return notificationLogRepository.save(notificationData);
    }
    
    @Transactional(readOnly = true)
    public NotificationLogEntity findById(UUID id) {
        return notificationLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("NotificationLog não encontrado com ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<NotificationLogEntity> findByUser(UUID userId, Pageable pageable) {
        return notificationLogRepository.findAll((root, query, cb) -> 
            cb.equal(root.get("user").get("id"), userId), pageable
        );
    }
    
    @Transactional(readOnly = true)
    public Page<NotificationLogEntity> findAll(Pageable pageable) {
        return notificationLogRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<NotificationLogEntity> findByUserAndStatus(UUID userId, NotificationStatus status, Pageable pageable) {
        return notificationLogRepository.findAll((root, query, cb) -> 
            cb.and(
                cb.equal(root.get("user").get("id"), userId),
                cb.equal(root.get("status"), status)
            ), pageable
        );
    }
    
    @Transactional
    public NotificationLogEntity markAsSent(UUID id) {
        NotificationLogEntity notification = findById(id);
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(OffsetDateTime.now());
        return notificationLogRepository.save(notification);
    }
    
    @Transactional
    public NotificationLogEntity markAsFailed(UUID id, String errorMessage) {
        NotificationLogEntity notification = findById(id);
        notification.setStatus(NotificationStatus.FAILED);
        notification.setErrorMessage(errorMessage);
        return notificationLogRepository.save(notification);
    }
}
