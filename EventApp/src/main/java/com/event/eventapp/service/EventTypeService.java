package com.event.eventapp.service;

import com.event.eventapp.model.EventType;
import com.event.eventapp.model.Product;
import com.event.eventapp.repository.EventTypeRepository;
import com.event.eventapp.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventTypeService {

    private final EventTypeRepository eventTypeRepository;

    public EventTypeService(EventTypeRepository eventTypeRepository) {
        this.eventTypeRepository = eventTypeRepository;
    }

    public List<EventType> getAllEventTypes() {
        return eventTypeRepository.findAll();
    }

    public Set<EventType> findByIds(Set<Long> ids) {
        return new HashSet<>(eventTypeRepository.findAllById(ids));
    }
}
