package com.university.tracker.model;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

// Класс - первичный составной ключ
@Embeddable
public class TaskTagId {
    public TaskTagId() {} // Hibernate требует пустой класс для создания объектов через рефлексию

    public TaskTagId(UUID taskId, UUID tagId) {
        this.taskId = taskId;
        this.tagId = tagId;
    }

    private UUID taskId;
    private UUID tagId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TaskTagId taskTagId)) return false;
        return Objects.equals(taskId, taskTagId.taskId) && Objects.equals(tagId, taskTagId.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, tagId);
    }
}
