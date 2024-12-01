package com.stylit.online.controller;

import com.stylit.online.dto.MessageDTO;
import com.stylit.online.service.MessageService;
import com.stylit.online.ApiResponse.ApiSuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/courier/chat")
public class CourierChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @Autowired
    public CourierChatController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    /**
     * Endpoint to send a private message.
     *
     * @param messageDTO The message details.
     * @return ResponseEntity with the saved message.
     */
    @PostMapping("/send")
    public ResponseEntity<ApiSuccessResponse> sendMessage(@RequestBody MessageDTO messageDTO) {
        // Save the message in the database
        MessageDTO savedMessage = messageService.saveMessage(messageDTO);

        // Notify the recipient in real-time
        messagingTemplate.convertAndSendToUser(
                messageDTO.getRecipientId(),
                "/queue/private-messages",
                savedMessage
        );

        return ResponseEntity.ok(
                ApiSuccessResponse.builder()
                        .message("Message sent successfully.")
                        .data(Map.of("message", savedMessage))
                        .build()
        );
    }

    /**
     * Endpoint to retrieve chat history between two users.
     *
     * @param senderId    The sender's ID.
     * @param recipientId The recipient's ID.
     * @return ResponseEntity with chat history.
     */
    @GetMapping("/history")
    public ResponseEntity<ApiSuccessResponse> getChatHistory(
            @RequestParam String senderId,
            @RequestParam String recipientId) {
        List<MessageDTO> chatHistory = messageService.getChatHistory(senderId, recipientId);

        return ResponseEntity.ok(
                ApiSuccessResponse.builder()
                        .message("Chat history retrieved successfully.")
                        .data(Map.of("chatHistory", chatHistory))
                        .build()
        );
    }

    /**
     * Endpoint to fetch messages for a specific recipient.
     *
     * @param recipientId The recipient's ID.
     * @return ResponseEntity with messages.
     */
    @GetMapping("/messages/{recipientId}")
    public ResponseEntity<ApiSuccessResponse> getMessagesByRecipient(@PathVariable String recipientId) {
        List<MessageDTO> messages = messageService.getMessagesByRecipient(recipientId);

        return ResponseEntity.ok(
                ApiSuccessResponse.builder()
                        .message("Messages retrieved successfully.")
                        .data(Map.of("messages", messages))
                        .build()
        );
    }

    @GetMapping("/unread-count/{recipientId}")
    public ResponseEntity<ApiSuccessResponse> getUnreadMessageCount(@PathVariable String recipientId) {
        long unreadCount = messageService.getUnreadMessageCount(recipientId);
        return ResponseEntity.ok(
                ApiSuccessResponse.builder()
                        .message("Unread message count retrieved successfully")
                        .data(Map.of("unreadCount", unreadCount))
                        .build()
        );
    }

    @PostMapping("/mark-as-read")
    public ResponseEntity<ApiSuccessResponse> markMessagesAsRead(
            @RequestParam String senderId,
            @RequestParam String recipientId) {
        messageService.markMessagesAsRead(senderId, recipientId);
        return ResponseEntity.ok(
                ApiSuccessResponse.builder()
                        .message("Messages marked as read successfully")
                        .build()
        );
    }
}
