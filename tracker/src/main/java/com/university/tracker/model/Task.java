package com.university.tracker.model;

import com.university.tracker.model.enums.Priority;
import com.university.tracker.model.enums.TaskStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")

public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @ColumnDefault("0")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToOne(mappedBy ="task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // @BatchSize - оказывается не работает на OneToOne, только на OneToMany и ManyToOne
    private TaskDescriptionDetails details;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskTag> tags = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Project getProject() {
        return project;
    }

    public Long getVersion() {
        return version;
    }

    public User getAssignee() {
        return assignee;
    }

    public TaskDescriptionDetails getDetails() {
        return details;
    }

    public List<TaskTag> getTags() {
        return tags;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public void setDetails(TaskDescriptionDetails details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id != null && id.equals(task.id);
    }

    @Override
    public int hashCode() {
        // Не Object.hash(id) потому что id у новой энтити равен null до вызова em.persist() из-за чего объект физически есть в Set, но найти невозмжно после генерации нового UUID при em.persist()
        return getClass().hashCode();
    }

    public void addTag(Tag tag) {
        TaskTag taskTag = new TaskTag(this, tag);
        tags.add(taskTag);
    }
}
