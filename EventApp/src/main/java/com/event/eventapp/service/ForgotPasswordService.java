package com.event.eventapp.service;

import com.event.eventapp.model.ForgotPasswordToken;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ForgotPasswordService {
    @Autowired
    JavaMailSender javaMailSender;

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    private final int MINUTES = 10;
    public LocalDateTime expireTimeRange() {
        return LocalDateTime.now().plusMinutes(MINUTES);
    }

    public void sendEmail(String to, String subject, String emailLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String emailContent = "<p>Bună ziua,</p>"
                + "Pentru a vă reseta parola contului pentru aplicaţia Planivent, vă rugăm accesaţi link-ul de mai jos."
                + "<p><a href=\"" + emailLink + "\">Schimbă parola</a></p>"
                + "<br>"
                + "Dacă nu aţi cerut resetarea parolei, ignoraţi mesajul.";

        helper.setText(emailContent, true);
        helper.setFrom("manadarinescu@gmail.com", "Echipa Planivent");
        helper.setSubject(subject);
        helper.setTo(to);
        javaMailSender.send(message);
    }

    public boolean isExpired(ForgotPasswordToken forgotPasswordToken) {
        return LocalDateTime.now().isAfter(forgotPasswordToken.getExpireTime());
    }

    public String checkValidity(ForgotPasswordToken forgotPasswordToken, Model model) {
        if (forgotPasswordToken == null) {
           model.addAttribute("error", "Token invalid");
           return "error-page";
        } else if (forgotPasswordToken.isUsed()){
            model.addAttribute("error", "Tokenul a fost deja folosit!");
            return "error-page";
        }
        else if (isExpired(forgotPasswordToken)) {
            model.addAttribute("error", "Tokenul a expirat");
            return "error-page";
        }
        else {
            return "reset-password";
        }
    }
}
