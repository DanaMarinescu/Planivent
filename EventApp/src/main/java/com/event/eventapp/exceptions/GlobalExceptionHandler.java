package com.event.eventapp.exceptions;

import jakarta.persistence.NonUniqueResultException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NonUniqueResultException.class)
    public ResponseEntity<Object> handleNonUniqueResultException(NonUniqueResultException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("The data provided is not unique as expected.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(Model model) {
        model.addAttribute("errorMessage", "The requested resource was not found.");
        return "error-page";
    }
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(Model model, UserNotFoundException e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error-page";
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTaskNotFoundException(Model model, TaskNotFoundException e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error-page";
    }
}
