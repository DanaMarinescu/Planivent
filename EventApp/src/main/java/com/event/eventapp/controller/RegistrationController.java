package com.event.eventapp.controller;

import com.event.eventapp.DTO.UserRegisteredDTO;
import com.event.eventapp.exceptions.EmailExistsException;
import com.event.eventapp.service.DefaultUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private final DefaultUserService userService;

    public RegistrationController(DefaultUserService userService) {
        super();
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegisteredDTO userRegistrationDto() {
        return new UserRegisteredDTO();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") UserRegisteredDTO registrationDto, BindingResult result, Model model) {
        if (userService.emailExists(registrationDto.getEmail())) {
            result.rejectValue("email_id", "error.user", "Există deja un cont cu această adresă de email.");
            model.addAttribute("registrationError", "Există deja un cont cu această adresă de email.");
            return "register";
        }

        userService.save(registrationDto);
        return "redirect:/login";
    }

    @ExceptionHandler(EmailExistsException.class)
    public String handleEmailExists(Model model, EmailExistsException e) {
        model.addAttribute("message", e.getMessage());
        return "register";
    }
}
