package com.stylit.online.repository;

import com.stylit.online.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderIdAndRecipientId(String senderId, String recipientId);

    List<Message> findByRecipientId(String recipientId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipientId = :recipientId AND m.isRead = false")
    long countUnreadMessagesByRecipient(String recipientId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.senderId = :senderId AND m.recipientId = :recipientId AND m.isRead = false")
    void markMessagesAsRead(String senderId, String recipientId);

}
