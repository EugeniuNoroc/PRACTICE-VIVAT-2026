package com.university.tracker.service;

import com.university.tracker.model.Task;
import com.university.tracker.repository.ProjectRepository;
import com.university.tracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private AuditService auditService;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, AuditService auditService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.auditService = auditService;
    }

    // TODO (W3 review): AOP-подводный камень из day-5 описан в доке, но в коде не показан — AuditService
    //   вынесен в отдельный бин (что как раз ПРЯЧЕТ проблему). Добавить демо: self-вызов @Transactional-
    //   метода внутри одного класса, который молча не открывает новую транзакцию (this.method(), не proxy).
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
    public List<Task> findAll() {
        return taskRepository.findAll();
    }
}
