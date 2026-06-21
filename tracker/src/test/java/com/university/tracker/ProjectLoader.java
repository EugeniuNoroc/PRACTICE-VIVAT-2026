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
}