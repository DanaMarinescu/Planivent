package com.event.eventapp.service;

import com.event.eventapp.DTO.UserDTO;
import com.event.eventapp.DTO.UserRegisteredDTO;
import com.event.eventapp.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface DefaultUserService extends UserDetailsService {
    User save(UserRegisteredDTO userRegisteredDTO);
    User save(User user);
    User findByName(String name);
    User findByEmail(String email);
    boolean emailExists(String email);
    Long getUserIdByName(String name);
    User findById(Long userId);
}
