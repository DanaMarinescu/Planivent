package com.event.eventapp.controller;
import com.event.eventapp.DTO.ChatMessage;
import com.event.eventapp.DTO.MessageDTO;
import com.event.eventapp.DTO.UserDTO;
import com.event.eventapp.model.*;
import com.event.eventapp.repository.RoleRepository;
import com.event.eventapp.repository.UserRepository;
import com.event.eventapp.service.CategoryService;
import com.event.eventapp.service.EmailService;
import com.event.eventapp.service.MessageService;
import com.event.eventapp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductService productService;
    private final EmailService emailService;

    public MessageController(MessageService messageService, CategoryService categoryService, UserRepository userRepository, RoleRepository roleRepository, ProductService productService, EmailService emailService) {
        this.messageService = messageService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productService = productService;
        this.emailService = emailService;
    }

    @ModelAttribute("allCategories")
    public List<Category> getAllCategories() {
        return categoryService.findAllCategories();
    }

    @GetMapping
    public String viewMessages(Model model) {
        List<Message> messages = messageService.findAllMessages();
        model.addAttribute("messages", messages);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername());
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("roles", roleRepository.findAll());
                return "messages";
            } else {
                model.addAttribute("error", "Utilizator negăsit!");
            }
            return "error-page";
        } else {
            model.addAttribute("error", "Eroare de încărcare a blogului!");
            return "error-page";
        }
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsersWithMessages() {
        User user = getAuthenticatedUser();

        List<UserDTO> userDTOs = messageService.findUsersWithMessagesToAndFrom(user.getId());

        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<MessageDTO>> getMessageHistory(@PathVariable Long userId) {
        User user = getAuthenticatedUser();

        List<Message> messages = messageService.findMessagesBetweenUsers(user.getId(), userId);
        List<MessageDTO> messageDTOs = messages.stream()
                .map(MessageDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(messageDTOs);
    }

    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        User sender = getAuthenticatedUser();
        User recipient = userRepository.findById(messageDTO.getRecipientId())
                .orElseThrow(() -> new UsernameNotFoundException("Receptor negăsit."));

        Product product = productService.findById(messageDTO.getProductId());
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Message message = getMessage(messageDTO, sender, recipient, product);
        Message savedMessage = messageService.saveMessage(message);
        emailService.sendNotificationEmailFromChat(recipient, message);

        MessageDTO responseDTO = new MessageDTO(savedMessage);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    private static Message getMessage(MessageDTO messageDTO, User sender, User recipient, Product product) {
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setProduct(product);
        message.setMessageText(messageDTO.getMessageText());
        message.setEventName(messageDTO.getEventName());
        message.setStartDate(LocalDateTime.now());
        return message;
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long userId) {
        User authenticatedUser = getAuthenticatedUser();
        messageService.deleteMessagesBetweenUsers(authenticatedUser.getId(), userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public User getAuthenticatedUser() throws UsernameNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());

        if (user == null) {
            throw new UsernameNotFoundException("Utilizator negăsit.");
        }
        return user;
    }
}
