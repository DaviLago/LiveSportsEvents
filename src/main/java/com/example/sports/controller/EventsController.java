package com.example.sports.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.sports.dto.event.EventPostDto;
import com.example.sports.dto.event.EventPostResponseDto;
import com.example.sports.model.Event;
import com.example.sports.service.EventService;
import com.example.sports.service.LiveService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/events")
public class EventsController {

    @Autowired
    private EventService eventService;

    @Autowired
    private LiveService liveService;

    @PostMapping
    public ResponseEntity<EventPostResponseDto> createEvent(@Valid @RequestBody EventPostDto dto) {
        log.info("Creating/updating event with request: {}", dto);
        Event event = eventService.save(new Event(dto));
        liveService.update(event);
        log.info("Event created/updated with ID: {}", event.getId());
        return ResponseEntity.ok(event.toResponseDto());
    }

}
