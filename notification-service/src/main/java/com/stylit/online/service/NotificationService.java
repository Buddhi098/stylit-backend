package com.stylit.online.service;

import com.stylit.online.dto.NotificationDTO;
import com.stylit.online.model.Notification;
import com.stylit.online.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepo notificationRepo;

    @Autowired
    public NotificationService(NotificationRepo notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    @Transactional
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = new Notification(
                notificationDTO.getMessage(),
                notificationDTO.getActorType(),
                notificationDTO.getUserId(),
                notificationDTO.getSubject()
        );
        Notification savedNotification = notificationRepo.save(notification);
        return mapToDTO(savedNotification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsForActor(String actorType, Long userId) {
        List<Notification> notifications = notificationRepo.findByActorTypeAndUserId(actorType, userId);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDTO updateReadStatus(Long id, boolean read) {
        Notification notification = notificationRepo.findById(id).orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(read);
        Notification updatedNotification = notificationRepo.save(notification);
        return mapToDTO(updatedNotification);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getMessage(),
                notification.getActorType(),
                notification.getUserId(),
                notification.getTimestamp(),
                notification.isRead(), // New field
                notification.getSubject() // New field
        );
    }
}