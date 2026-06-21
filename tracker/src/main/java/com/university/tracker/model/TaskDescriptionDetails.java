package com.university.tracker.model;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.UUID;

@Entity
@Table(name = "task_description_details")
public class TaskDescriptionDetails {
    public TaskDescriptionDetails() {}

    public TaskDescriptionDetails(Task task, String markdownContent) {
        this.task = task;
        this.markdownContent = markdownContent;
    }

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(columnDefinition = "TEXT")
    private String markdownContent;

    public UUID getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public String getMarkdownContent() {
        return markdownContent;
    }

    public void setMarkdownContent(String markdownContent) {
        this.markdownContent = markdownContent;
    }
}
