package com.university.tracker.repository;

import com.university.tracker.model.Task;
import com.university.tracker.model.User;
import com.university.tracker.model.enums.Priority;
import com.university.tracker.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByAssignee(User assignee);
    List<Task> findByProjectIdAndStatus(UUID projectId, TaskStatus status);
    List<Task> findByDueDateBefore(LocalDateTime dueDate);
    long countByStatus(TaskStatus status);
    boolean existsByTitleAndProjectId(String title, UUID projectId);

    @Query("SELECT t FROM Task t WHERE t.priority = :priority AND t.status <> 'DONE'")
    List<Task> findByPriorityAndStatus(@Param("priority") Priority priority);

    @Query(value = "SELECT status, COUNT(*) FROM tasks GROUP BY status", nativeQuery = true)
    List<Object[]> findAllTaskStatus();
}
