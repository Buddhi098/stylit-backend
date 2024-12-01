package com.stylit.online.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private String senderId;
    private String recipientId;
    private String content;
    private String timestamp; // Can be formatted for frontend display
    private boolean isRead;
}
