package com.event.eventapp.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Utilizator negăsit!");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
