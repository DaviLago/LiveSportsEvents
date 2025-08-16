package com.example.sports.dto.event;

import com.example.sports.enums.Status;

public record EventPostResponseDto(
    String eventId,
    Status status
) {}
