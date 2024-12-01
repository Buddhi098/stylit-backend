package com.stylit.online.service;

import com.stylit.online.dto.MessageDTO;
import com.stylit.online.model.Message;
import com.stylit.online.repository.MessageRepository;
import com.stylit.online.util.MessageMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Save a private message.
     *
     * @param messageDTO The message data transfer object.
     * @return The saved message as a DTO.
     */
    public MessageDTO saveMessage(MessageDTO messageDTO) {
        validateMessageDTO(messageDTO);

        // Map DTO to entity, set the timestamp, and save
        Message message = MessageMapper.toEntity(messageDTO);
        message.setTimestamp(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);

        // Return the saved message as DTO
        return MessageMapper.toDTO(savedMessage);
    }

    /**
     * Retrieve the chat history between two users.
     *
     * @param senderId    The sender's ID.
     * @param recipientId The recipient's ID.
     * @return A list of chat messages as DTOs.
     */
    public List<MessageDTO> getChatHistory(String senderId, String recipientId) {
        // Fetch chat history (bidirectional)
        List<Message> chatHistory = messageRepository.findBySenderIdAndRecipientId(senderId, recipientId);
        chatHistory.addAll(messageRepository.findBySenderIdAndRecipientId(recipientId, senderId));

        // Sort messages by timestamp
        chatHistory.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

        // Convert entities to DTOs and return
        return chatHistory.stream()
                .map(MessageMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve messages for a specific recipient.
     *
     * @param recipientId The recipient's ID.
     * @return A list of messages as DTOs.
     */
    public List<MessageDTO> getMessagesByRecipient(String recipientId) {
        // Fetch messages by recipient
        List<Message> messages = messageRepository.findByRecipientId(recipientId);

        // Convert entities to DTOs and return
        return messages.stream()
                .map(MessageMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Validates the provided MessageDTO.
     *
     * @param messageDTO The message DTO to validate.
     */
    private void validateMessageDTO(MessageDTO messageDTO) {
        if (messageDTO.getSenderId() == null || messageDTO.getSenderId().isEmpty()) {
            throw new IllegalArgumentException("Sender ID cannot be null or empty.");
        }
        if (messageDTO.getRecipientId() == null || messageDTO.getRecipientId().isEmpty()) {
            throw new IllegalArgumentException("Recipient ID cannot be null or empty.");
        }
        if (messageDTO.getContent() == null || messageDTO.getContent().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be null or empty.");
        }
    }

    public long getUnreadMessageCount(String recipientId) {
        return messageRepository.countUnreadMessagesByRecipient(recipientId);
    }

    @Transactional
    public void markMessagesAsRead(String senderId, String recipientId) {
        messageRepository.markMessagesAsRead(senderId, recipientId);
    }
}
