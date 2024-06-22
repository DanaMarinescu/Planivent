package com.event.eventapp.service;

import com.event.eventapp.model.Message;
import com.event.eventapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendNotificationEmail(User recipient, Message message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipient.getEmail());
        mailMessage.setSubject("Aveţi un mesaj nou!");
        mailMessage.setText("Aţi primit un mesaj nou pentru evenimentul: " + message.getEventName() +
                "\nConţinut: " + message.getMessageText() +
                "\nDe la: " + message.getSender().getEmail());

        mailSender.send(mailMessage);
    }

    public void sendNotificationEmailFromChat(User recipient, Message message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipient.getEmail());
        mailMessage.setSubject("Aveţi un mesaj nou!");
        mailMessage.setText("Aţi primit un mesaj nou: " +
                "\nConţinut: " + message.getMessageText() +
                "\nDe la: " + message.getSender().getEmail());

        mailSender.send(mailMessage);
    }
}
