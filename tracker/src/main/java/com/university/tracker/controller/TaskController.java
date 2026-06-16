package com.university.tracker.controller;

import com.university.tracker.model.Task;
import com.university.tracker.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskRepository taskRepository;
    public  TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Operation(summary = "Получить все таски")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Таски найдены, возвращены данные")
    })
    @GetMapping
    public List<Task> findAll() {
        return taskRepository.findAll();
    }
}
