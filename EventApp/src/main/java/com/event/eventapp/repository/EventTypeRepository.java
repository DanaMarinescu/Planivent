package com.event.eventapp.repository;

import com.event.eventapp.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long> {
}
