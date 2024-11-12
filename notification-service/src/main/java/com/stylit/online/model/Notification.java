package com.stylit.online.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Actor Type is required")
    private String actorType;

    private Long userId;

    private LocalDateTime timestamp;

    @Column(name = "`read`")
    private boolean read; // New field

    @NotBlank(message = "Subject is required")
    private String subject; // New field

    public Notification(String message, String actorType, Long userId, String subject) {
        this.message = message;
        this.actorType = actorType;
        this.userId = userId;
        this.subject = subject;
        this.timestamp = LocalDateTime.now();
        this.read = false; // Default to false
    }

    // Getters and Setters
}