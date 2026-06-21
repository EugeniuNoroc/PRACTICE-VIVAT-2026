package com.university.tracker.repository;

import com.university.tracker.dto.ProjectSummaryDto;
import com.university.tracker.model.Project;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    // решает N+1 для tasks, но оставляет проблему с details
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.tasks")
    List<Project> findAllWithTasks();

    // решает N+1 для tasks И для details
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.tasks t LEFT JOIN FETCH t.details")
    List<Project> findAllWithTasksAndDetails();

    @NullMarked
    // В комментарии для теста BatchSize
    // @EntityGraph(attributePaths = {"tasks", "tasks.details"})
    @Override
    List<Project> findAll();

    @Query("SELECT p.id AS id, p.name AS name, COUNT(t) AS taskCount " + "FROM Project p LEFT JOIN p.tasks t GROUP BY p.id, p.name")
    List<ProjectSummaryDto> findProjectSummaries();
}
