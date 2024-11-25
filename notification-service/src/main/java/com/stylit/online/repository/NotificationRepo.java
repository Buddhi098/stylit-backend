package com.stylit.online.repository;

import com.stylit.online.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

    List<Notification> findByActorTypeAndUserId(String actorType, Long userId);

    List<Notification> findByActorTypeAndUserIdAndRead(String actorType, Long userId, boolean read); // New method
}