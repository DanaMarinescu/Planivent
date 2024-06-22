package com.event.eventapp.controller;

import com.event.eventapp.DTO.UserLoginDTO;
import com.event.eventapp.service.DefaultUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private DefaultUserService userService;

    @ModelAttribute("user")
    public UserLoginDTO userLoginDTO() {
        return new UserLoginDTO();
    }

    @GetMapping
    public String login() {
        return "login";
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO userLoginDTO,  HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        try {
            userService.loadUserByUsername(userLoginDTO.getUsername());
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                    .body("Utilizator autentificat cu succes.");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                    .body("Email sau parolă greşite. Te rog, încearcă din nou.");
        }
    }
}
