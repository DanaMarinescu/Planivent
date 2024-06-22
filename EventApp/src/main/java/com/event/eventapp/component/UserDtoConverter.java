package com.event.eventapp.component;

import com.event.eventapp.DTO.UserDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserDtoConverter implements Converter<Map<String, Object>, UserDTO> {
    @Override
    public UserDTO convert(Map<String, Object> source) {
        UserDTO dto = new UserDTO();
        dto.setId((Long) source.get("id"));
        dto.setEmail((String) source.get("email"));
        dto.setName((String) source.get("name"));
        return dto;
    }
}
