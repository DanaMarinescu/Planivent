package com.event.eventapp.service;

import com.event.eventapp.DTO.EventDTO;
import com.event.eventapp.exceptions.UserNotFoundException;
import com.event.eventapp.exceptions.UserTaskAlreadyExistsException;
import com.event.eventapp.model.Event;
import com.event.eventapp.model.User;
import com.event.eventapp.model.UserTask;
import com.event.eventapp.repository.EventRepository;
import com.event.eventapp.repository.UserRepository;
import com.event.eventapp.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;


    public List<Event> getEventsByUserId(Long userId) {
        return eventRepository.findByUserId(userId);
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public Event addEvent(EventDTO eventDTO, String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Utilizator negăst.");
        }

        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDateTime(eventDTO.getDate().atZoneSameInstant(ZoneId.of("Europe/Bucharest")).toLocalDateTime());
        event.setDescription(eventDTO.getDescription());
        event.setUser(user);

        return eventRepository.save(event);
    }

    public List<Event> getAllEventsByUserId(Long userId) {
        return eventRepository.findByUserId(userId);
    }
}
