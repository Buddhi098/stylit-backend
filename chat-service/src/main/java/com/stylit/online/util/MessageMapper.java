package com.stylit.online.util;

import com.stylit.online.dto.MessageDTO;
import com.stylit.online.model.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static MessageDTO toDTO(Message message) {
        return MessageDTO.builder()
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .content(message.getContent())
                .timestamp(message.getTimestamp().format(formatter))
                .isRead(message.isRead())
                .build();
    }

    public static Message toEntity(MessageDTO dto) {
        return Message.builder()
                .senderId(dto.getSenderId())
                .recipientId(dto.getRecipientId())
                .content(dto.getContent())
                .timestamp(LocalDateTime.parse(dto.getTimestamp(), formatter))
                .isRead(dto.isRead())
                .build();
    }
}
