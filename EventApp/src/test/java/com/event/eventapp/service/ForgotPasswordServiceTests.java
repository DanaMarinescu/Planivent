package com.event.eventapp.service;

import com.event.eventapp.model.ForgotPasswordToken;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.Model;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ForgotPasswordServiceTests {

    @InjectMocks
    private ForgotPasswordService forgotPasswordService;

    @Test
    void generateTokenTest() {
        String token = forgotPasswordService.generateToken();
        assertNotNull(token);
        assertEquals(36, token.length());
    }

    @Test
    void expireTimeRangeTest() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = forgotPasswordService.expireTimeRange();
        assertTrue(now.plusMinutes(10).minusSeconds(1).isBefore(expireTime));
        assertTrue(now.plusMinutes(10).plusSeconds(1).isAfter(expireTime));
    }

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void sendEmailTest() throws MessagingException, UnsupportedEncodingException {
        MimeMessage mockMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

        forgotPasswordService.sendEmail("test@example.com", "Test Subject", "http://testlink.com");
        verify(javaMailSender, times(1)).send(mockMessage);
    }

    @Test
    void isExpiredTest() {
        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setExpireTime(LocalDateTime.now().minusMinutes(1));
        assertTrue(forgotPasswordService.isExpired(token));
    }

    @Test
    void checkValidityTest() {
        Model mockModel = Mockito.mock(Model.class);
        ForgotPasswordToken token = new ForgotPasswordToken();

        assertEquals("error-page", forgotPasswordService.checkValidity(null, mockModel));
        verify(mockModel).addAttribute("error", "Token invalid");

        Mockito.reset(mockModel);

        token.setUsed(true);
        assertEquals("error-page", forgotPasswordService.checkValidity(token, mockModel));
        verify(mockModel).addAttribute("error", "Tokenul a fost deja folosit!");

        Mockito.reset(mockModel);

        token.setUsed(false);
        token.setExpireTime(LocalDateTime.now().minusMinutes(11));
        assertEquals("error-page", forgotPasswordService.checkValidity(token, mockModel));
        verify(mockModel).addAttribute("error", "Tokenul a expirat");

        Mockito.reset(mockModel);

        token.setExpireTime(LocalDateTime.now().plusMinutes(11));
        assertEquals("reset-password", forgotPasswordService.checkValidity(token, mockModel));
    }
}
