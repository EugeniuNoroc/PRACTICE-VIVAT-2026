package com.university.tracker;

import com.university.tracker.model.Project;
import com.university.tracker.model.Task;
import com.university.tracker.repository.ProjectRepository;
import com.university.tracker.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
public class ProjectLoader {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    public UUID saveProjectWithTasks() {
        Project project = new Project();
        projectRepository.save(project);
        Task task = new Task();
        task.setTitle("Test Task");
        task.setProject(project);
        taskRepository.save(task);
        return project.getId();
    }

    @Transactional
    public Project loadProject(UUID id) {
        return projectRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Project saveProject() {
        Project project = new Project();
        return projectRepository.save(project);
    }

    @Transactional
    public Task saveTask(UUID projectId) {
        Task task = new Task();
        task.setTitle("Original");
        task.setProject(projectRepository.findById(projectId).orElseThrow());
        return taskRepository.save(task);
    }

    @Transactional
    public Task loadTask(UUID id) {
        return taskRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Task updateTask(UUID id, String newTitle) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setTitle(newTitle);
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTaskEntity(Task task, String newTitle) {
        task.setTitle(newTitle);
        return taskRepository.save(task);
    }

    @Transactional
    public Task loadTaskWithLock(UUID id) {
        return taskRepository.findByIdForUpdate(id).orElseThrow();
    }
}