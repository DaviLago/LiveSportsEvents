package com.example.sports.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sports.model.Event;
import com.example.sports.repository.EventRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event save(Event event) {
        log.info("Updated event {} status to {} and score to {}", event.getId(), event.getStatus(), event.getCurrentScore());
        return eventRepository.save(event);
    }

    public Optional<Event> findById(String id) {
        Optional<Event> oEvent = eventRepository.findById(id);
        if(oEvent.isEmpty()){
            log.warn("Event {} not found", id);
        }
        return oEvent;
    }

}
