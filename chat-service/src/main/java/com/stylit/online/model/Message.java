package com.stylit.online.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String senderId; // User sending the message

    @Column(nullable = false)
    private String recipientId; // User receiving the message

    @Column(nullable = false)
    private String content; // The message text

    @Column(nullable = false)
    private LocalDateTime timestamp; // Time of the message

    @Column(nullable = false)
    private boolean isRead = false;
}
