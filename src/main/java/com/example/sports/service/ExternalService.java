package com.example.sports.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sports.dto.external.ExternalEventInfo;
import com.example.sports.model.Event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExternalService {

    @Autowired
    private EventService eventService;

    public Optional<ExternalEventInfo> fetchEventInfo(String eventId) {
        Optional<Event> oEvent = eventService.findById(eventId);
        if (oEvent.isPresent()) {
            Event event = oEvent.get();
            Integer currentScore = event.getCurrentScore() != null ? event.getCurrentScore() : 0;
            Integer newScore = currentScore + 1;
            event.setCurrentScore(newScore);
            eventService.save(event);
            log.info("Event {} found, returning success response", eventId);
            return Optional.of(new ExternalEventInfo(eventId, newScore));
        }
        log.warn("Event {} not found, returning empty response", eventId);
        return Optional.empty();
    }

}
