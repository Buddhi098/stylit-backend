package com.stylit.online.controller;

import com.stylit.online.dto.NotificationDTO;
import com.stylit.online.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("public/notification")
@Validated
@RequiredArgsConstructor
public class PublicNotificationController {

    @Autowired
    private final NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO) {
        NotificationDTO createdNotification = notificationService.createNotification(notificationDTO);
        return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
    }

    @GetMapping("/{actorType}/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsForActor(
            @PathVariable String actorType,
            @PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsForActor(actorType, userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> updateReadStatus(
            @PathVariable Long id,
            @RequestParam boolean read) {
        NotificationDTO updatedNotification = notificationService.updateReadStatus(id, read);
        return new ResponseEntity<>(updatedNotification, HttpStatus.OK);
    }
}