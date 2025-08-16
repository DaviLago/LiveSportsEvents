package com.example.sports.dto.event;

import com.example.sports.enums.Status;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record EventPostDto(
    @NotEmpty(message = "Event ID cannot be empty") String eventId,
    @NotNull(message = "Status cannot be empty") Status status
) {}
