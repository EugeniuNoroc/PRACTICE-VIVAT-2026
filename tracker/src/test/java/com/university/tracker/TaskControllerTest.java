package com.university.tracker;

import com.university.tracker.controller.TaskController;
import com.university.tracker.dto.*;
import com.university.tracker.model.enums.Priority;
import com.university.tracker.model.enums.TaskStatus;
import com.university.tracker.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = TaskController.class)
@ActiveProfiles("test")
@AutoConfigureRestTestClient
public class TaskControllerTest {

    @MockitoBean
    private TaskService taskService;

    @Autowired
    RestTestClient client;

    @Test
    void findAll_shouldReturnCode200_ifTasksExists() {
        ProjectSummary projectSummary = new ProjectSummary() {
            @Override
            public UUID getId() {
                return UUID.fromString("c9631ba6-a2fe-4451-97ef-0ac35f019d7a");
            }

            @Override
            public String getName() {
                return "name";
            }

            @Override
            public String getDescription() {
                return "description";
            }

            @Override
            public LocalDateTime getCreatedAt() {
                return LocalDateTime.now();
            }
        };

        UserSummary userSummary = new UserSummary() {
            @Override
            public UUID getId() {
                return UUID.fromString("3f8b9bdf-03dc-46d0-8db0-8ffcfc4ed4e3");
            }
            @Override
            public String getUsername(){
                return "username";
            }
            @Override
            public String getEmail(){
                return "email";
            }
        };

        TaskSummary taskSummary = new TaskSummary() {
            @Override
            public UUID getId() {
                return UUID.randomUUID();
            }
            @Override
            public  String getTitle() {
                return "title";
            }
            @Override
            public String getDescription() {
                return "description";
            }
            @Override
            public TaskStatus getStatus() {
                return TaskStatus.DONE;
            }
            @Override
            public Priority getPriority() {
                return Priority.MEDIUM;
            }
            @Override
            public LocalDateTime getCreatedAt() {
                return LocalDateTime.now();
            }
            @Override
            public LocalDateTime getDueDate() {
                return LocalDateTime.of(2028, Month.APRIL, 21, 23, 59);
            }
            @Override
            public ProjectSummary getProject() {
                return projectSummary;
            }
            @Override
            public UserSummary getAssignee() {
                return userSummary;
            }
        };

        when(taskService.findAll()).thenReturn(List.of(taskSummary));

        client.get()
                .uri("/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .value(task -> {
                    assert task != null;
                    assertThat(task.getFirst().get("title")).isEqualTo("title");
                    Map<String, Object> project = (Map<String, Object>) task.getFirst().get("project");
                    assertThat(project.get("name")).isEqualTo("name");
                    Map<String, Object> assignee = (Map<String, Object>) task.getFirst().get("assignee");
                    assertThat(assignee.get("username")).isEqualTo("username");
                });
    }
}
