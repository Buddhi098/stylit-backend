package com.stylit.online.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Actor Type is required")
    private String actorType;

    private Long userId;

    private LocalDateTime timestamp;

    private boolean read; // New field

    @NotBlank(message = "Subject is required")
    private String subject; // New field
}