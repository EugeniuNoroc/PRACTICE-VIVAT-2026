package com.university.tracker.service;

import com.university.tracker.dto.ProjectResponseDto;
import com.university.tracker.dto.TaskResponseDto;
import com.university.tracker.dto.TaskSummary;
import com.university.tracker.dto.UserResponseDto;
import com.university.tracker.model.Project;
import com.university.tracker.model.Task;
import com.university.tracker.model.User;
import com.university.tracker.repository.ProjectRepository;
import com.university.tracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final AuditService auditService;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, AuditService auditService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.auditService = auditService;
    }

    @Transactional
    public Task create(String title, UUID projectId, boolean auditShouldFail) {
        Task task = new Task();
        task.setTitle(title);
        task.setProject(projectRepository.findById(projectId).get());
        Task savedTask = taskRepository.save(task);
        try {
            auditService.logAudit("SAVED TASK", savedTask.getId(), auditShouldFail);
        } catch (RuntimeException e) {
            System.out.println("Аудит упал, но таск сохраняем: " + e.getMessage());
        }
        return savedTask;
    }

    @Transactional(readOnly = true)
    public List<TaskSummary> findAll() {
        return taskRepository.findAllBy();
    }
}
