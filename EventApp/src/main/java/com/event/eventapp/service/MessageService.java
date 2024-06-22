package com.event.eventapp.service;

import com.event.eventapp.DTO.UserDTO;
import com.event.eventapp.model.Message;
import com.event.eventapp.model.User;
import com.event.eventapp.repository.MessageRepository;
import com.event.eventapp.repository.UserProjection;
import com.event.eventapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    public List<UserDTO> findUsersWithMessagesToAndFrom(Long userId) {
        List<User> senders = messageRepository.findSendersByRecipientId(userId);

        List<User> recipients = messageRepository.findRecipientsBySenderId(userId);

        Set<User> combinedUsers = new HashSet<>();
        combinedUsers.addAll(senders);
        combinedUsers.addAll(recipients);

        return combinedUsers.stream()
                .map(user -> getUserDtoById(user.getId()))
                .collect(Collectors.toList());
    }

    private UserDTO getUserDtoById(Long userId) {
        UserProjection projection = userRepository.findUserProjectionById(userId);
        if (projection == null) {
            System.err.println("UserProjection este null pentru: " + userId);
            return new UserDTO(userId, "Unknown", "Unknown", "Unknown", "Unknown");
        }

        return new UserDTO(
                projection.getId(),
                projection.getName(),
                projection.getEmail(),
                projection.getPhone(),
                projection.getRole()
        );
    }

    public List<Message> findMessagesBetweenUsers(Long userId1, Long userId2) {
        return messageRepository.findMessagesBetweenUsers(userId1, userId2);
    }

    @Transactional
    public void deleteMessagesBetweenUsers(Long userId1, Long userId2) {
        List<Message> messages = messageRepository.findMessagesBetweenUsers(userId1, userId2);
        messageRepository.deleteAll(messages);
    }
}
