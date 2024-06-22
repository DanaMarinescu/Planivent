package com.event.eventapp.DTO;

import com.event.eventapp.model.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {
    private Long id;
    private String eventName;
    private String messageText;
    private LocalDateTime startDate;
    private String productName;
    private Long productId;
    private Long senderId;
    private Long recipientId;
    private UserDTO senderName;

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.eventName = message.getEventName();
        this.messageText = message.getMessageText();
        this.startDate = message.getStartDate();
        this.productId = message.getProduct() != null ? message.getProduct().getId() : null;
        this.productName = message.getProduct() != null ? message.getProduct().getName() : null;
        this.senderId = message.getSender() != null ? message.getSender().getId() : null;
        this.recipientId = message.getRecipient() != null ? message.getRecipient().getId() : null;
        this.senderName = new UserDTO(message.getSender());
    }
}
