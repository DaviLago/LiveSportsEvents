package com.example.sports.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.example.sports.dto.event.EventPostDto;
import com.example.sports.dto.event.EventPostResponseDto;
import com.example.sports.enums.Status;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private Integer currentScore;

    public Event(EventPostDto eventDtoPost) {
        id = eventDtoPost.eventId();
        status = eventDtoPost.status();
    }

    public EventPostResponseDto toResponseDto() {
        return new EventPostResponseDto(id, status);
    }



}
