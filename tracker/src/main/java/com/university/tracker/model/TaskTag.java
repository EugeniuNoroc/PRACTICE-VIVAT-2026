package com.university.tracker.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

// join entity, связующая таблица
@Entity
@Table(name = "task_tags")
public class TaskTag {
    public TaskTag() {} // Hibernate требует пустой класс для создания объектов через рефлексию

    public TaskTag(Task task, Tag tag) {
        this.task = task;
        this.tag = tag;
        this.id = new TaskTagId(task.getId(), tag.getId());
        this.addedAt = LocalDateTime.now();
    }

    @EmbeddedId
    private TaskTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId") // Поле task связано с частью taskId в составном ключе TaskTagId
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;

    private LocalDateTime addedAt;

    public TaskTagId getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public Tag getTag() {
        return tag;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
