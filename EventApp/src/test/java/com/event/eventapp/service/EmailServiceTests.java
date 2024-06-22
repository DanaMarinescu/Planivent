package com.event.eventapp.service;

import static org.mockito.Mockito.*;

import com.event.eventapp.model.Message;
import com.event.eventapp.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendNotificationEmail() {
        User recipient = new User();
        recipient.setEmail("recipient@example.com");
        Message message = new Message();
        message.setEventName("Event Name");
        message.setMessageText("Buna ziua, furnizor!");
        message.setSender(new User(1302L));

        emailService.sendNotificationEmail(recipient, message);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendNotificationEmailFromChat() {
        User recipient = new User();
        recipient.setEmail("recipient@example.com");
        Message message = new Message();
        message.setMessageText("Ai primit mesaj in chat!");
        message.setSender(new User(1302L));

        emailService.sendNotificationEmailFromChat(recipient, message);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
