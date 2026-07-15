package com.university.tracker.dto;

import com.university.tracker.model.enums.Priority;
import com.university.tracker.model.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TaskSummary {
    UUID getId();
    String getTitle();
    String getDescription();
    TaskStatus getStatus();
    Priority getPriority();
    LocalDateTime getCreatedAt();
    LocalDateTime getDueDate();
    ProjectSummary getProject();
    UserSummary getAssignee();
}
