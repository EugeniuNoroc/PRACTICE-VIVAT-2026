package com.university.tracker.dto;

import com.university.tracker.model.enums.Priority;
import com.university.tracker.model.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponseDto(UUID id, String title, String description, TaskStatus status, Priority priority, LocalDateTime createdAt, LocalDateTime dueDate, ProjectResponseDto project, UserResponseDto assignee) {}