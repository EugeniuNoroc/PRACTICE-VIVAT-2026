package com.university.tracker.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponseDto(UUID id, String name, String description, LocalDateTime createdAt) {
}
