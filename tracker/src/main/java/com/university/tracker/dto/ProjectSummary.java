package com.university.tracker.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProjectSummary {
    UUID getId();
    String getName();
    String getDescription();
    LocalDateTime getCreatedAt();
}
