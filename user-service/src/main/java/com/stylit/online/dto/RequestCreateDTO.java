package com.stylit.online.dto;

import lombok.Data;

@Data
public class RequestCreateDTO {
    private Long senderId;
    private String senderRole;
    private Long receiverId;
    private String receiverRole;
}
