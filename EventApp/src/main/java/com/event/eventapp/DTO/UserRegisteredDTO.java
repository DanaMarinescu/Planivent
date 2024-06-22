package com.event.eventapp.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisteredDTO {
    private String name;
    private String email;
    private String password;
    private String role;
}
