package com.event.eventapp.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {
    private String username;
    private String password;

    public UserLoginDTO() {
        super();
    }
}
