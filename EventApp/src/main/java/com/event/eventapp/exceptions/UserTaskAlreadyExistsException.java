package com.event.eventapp.exceptions;

public class UserTaskAlreadyExistsException extends Throwable {
    public UserTaskAlreadyExistsException() {
        super("Utilizator negăsit!");
    }

    public UserTaskAlreadyExistsException(String message) {
        super(message);
    }
}
