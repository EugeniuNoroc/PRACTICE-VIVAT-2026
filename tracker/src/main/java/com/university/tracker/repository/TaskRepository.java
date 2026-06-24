package com.university.tracker.repository;

import com.university.tracker.model.Task;
import com.university.tracker.model.User;
import com.university.tracker.model.enums.Priority;
import com.university.tracker.model.enums.TaskStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    // TODO (W3 review): pessimisticLockingTest лишь проверяет, что запрос отрабатывает, а не семантику
    //   FOR UPDATE/конкуренции. Добавить тест с двумя транзакциями на реальную блокировку строки.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Task t WHERE t.id = :id")
    Optional<Task> findByIdForUpdate(@Param("id") UUID id);
}
