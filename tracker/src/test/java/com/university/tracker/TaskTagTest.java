package com.university.tracker;

import com.university.tracker.model.Project;
import com.university.tracker.model.Tag;
import com.university.tracker.model.Task;
import com.university.tracker.model.User;
import com.university.tracker.model.enums.Priority;
import com.university.tracker.model.enums.TaskStatus;
import com.university.tracker.repository.ProjectRepository;
import com.university.tracker.repository.TagRepository;
import com.university.tracker.repository.TaskRepository;
import com.university.tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskTagTest {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    UserRepository userRepository;
    @Test
    public void addTag_shouldAddTag_ifTagExists() {
        User user1 = new User();
        user1.setUsername("john_doe");
        user1.setEmail("john@example.com");
        userRepository.save(user1);

        Project project1 = new Project();
        project1.setName("Website Redesign");
        project1.setDescription("Redesign the company website");
        project1.setCreatedAt(LocalDateTime.now());
        projectRepository.save(project1);

        Tag tag1 = new Tag();
        tag1.setName("bug");
        tag1.setColor("#FF0000");
        tagRepository.save(tag1);

        Task task3 = new Task();
        task3.setTitle("Create login page");
        task3.setStatus(TaskStatus.IN_PROGRESS);
        task3.setCreatedAt(LocalDateTime.now());
        task3.setPriority(Priority.HIGH);
        task3.setProject(project1);
        task3.setAssignee(user1);
        taskRepository.save(task3);
        task3.addTag(tag1);
        taskRepository.save(task3);

        Task taskLoaded = taskRepository.findById(task3.getId()).orElseThrow();

        assertThat(taskLoaded.getTags()).hasSize(1);
        assertThat(taskLoaded.getTags().getFirst().getTag().getName()).isEqualTo("bug");
    }
}
