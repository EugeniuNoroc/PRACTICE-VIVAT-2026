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
    // TODO (W3 review): эндпоинт отдаёт сущности Task напрямую и падает с 500 на непустом ответе:
    //   Task.project — @ManyToOne(LAZY), open-in-view=false → Jackson сериализует proxy без сессии →
    //   LazyInitializationException ("Could not initialize proxy [Project]"). Проверено живым GET /tasks.
    //   Чинить: возвращать DTO (record) или @Query "... JOIN FETCH t.project"; OSIV обратно НЕ включать.
    //   Добавить @WebMvcTest на /tasks (контроллер сейчас не покрыт тестами). См. день 4.
    @GetMapping
    public List<Task> findAll() {
        return taskRepository.findAll();
    }
}
