package com.event.eventapp.controller;

import com.event.eventapp.model.ForgotPasswordToken;
import com.event.eventapp.repository.ForgotPasswordRepository;
import com.event.eventapp.service.DefaultUserService;
import com.event.eventapp.service.ForgotPasswordService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.event.eventapp.model.User;

import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {
    private final DefaultUserService defaultUserService;
    private final ForgotPasswordService forgotPasswordService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public ForgotPasswordController(DefaultUserService defaultUserService, ForgotPasswordService forgotPasswordService, ForgotPasswordRepository forgotPasswordRepository) {
        this.defaultUserService = defaultUserService;
        this.forgotPasswordService = forgotPasswordService;
        this.forgotPasswordRepository = forgotPasswordRepository;
    }

    @GetMapping("/password-request")
    public String passwordRequest() {
        return "password-request";
    }

    @PostMapping("/password-request")
    public String savePasswordRequest(@RequestParam("email") String email, Model model) throws MessagingException, UnsupportedEncodingException {
        User user = defaultUserService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Acest email nu are cont asociat!");
            return "password-request";
        }

        ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
        forgotPasswordToken.setExpireTime(forgotPasswordService.expireTimeRange());
        forgotPasswordToken.setToken(forgotPasswordService.generateToken());
        forgotPasswordToken.setUser(user);
        forgotPasswordToken.setUsed(false);

        forgotPasswordRepository.save(forgotPasswordToken);
        String emailLink = "http://localhost:8080/reset-password?token=" + forgotPasswordToken.getToken();

        try {
            forgotPasswordService.sendEmail(user.getEmail(), "Link pentru resetarea parolei Planivent", emailLink);
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Eroare la trimiserea mail-ului!");
            return "password-request";
        }

        return "redirect:/password-request?success";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@Param(value="token") String token, Model model, HttpSession session) {
        session.setAttribute("token", token);
        ForgotPasswordToken forgotPasswordToken = forgotPasswordRepository.findByToken(token);
        return forgotPasswordService.checkValidity(forgotPasswordToken, model);
    }

    @PostMapping("/reset-password")
    public String saveResetPassword(HttpServletRequest request, HttpSession session, Model model) {
        String password = request.getParameter("password");
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Parola nu poate fi goală!");
            return "reset-password";
        }

        String token = (String)session.getAttribute("token");

        ForgotPasswordToken forgotPasswordToken = forgotPasswordRepository.findByToken(token);
        if (forgotPasswordToken == null || forgotPasswordToken.isUsed()) {
            model.addAttribute("error", "Token invalid sau deja utilizat!");
            return "reset-password";
        }

        User user = forgotPasswordToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        forgotPasswordToken.setUsed(true);
        defaultUserService.save(user);
        forgotPasswordRepository.save(forgotPasswordToken);

        model.addAttribute("message", "Parolă resetată cu succes!");
        return "reset-password";
    }
}