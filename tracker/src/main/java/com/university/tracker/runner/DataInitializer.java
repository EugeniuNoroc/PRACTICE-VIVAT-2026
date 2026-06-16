package com.university.tracker.runner;

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
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;

    public DataInitializer(ProjectRepository projectRepository, TaskRepository taskRepository, TagRepository tagRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(String... args) {
        // Очищаем репозитории чтобы данные не дублировались, возможно можно как-то более филигранно, но добавлять проверку на каждую запись - смерти подобно
        // Если шлепнуть 4 ифа - тогда условно project1.getId() выдадут null -> NullPointerException
        // В теории можно подгружать все из бд каждый запуск, но будто бы сейчас deleteAll() + пересоздание more than enough
        // Можно еще в теории шлепнуть через deleteAllInBatch() вместо deleteAll() чтобы и проверок не было, но с проверками звучит правильнее, разница лишь в эффективности
        taskRepository.deleteAll();
        tagRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        userRepository.save(user);

        Project project1 = new Project();
        project1.setName("Website Redesign");
        project1.setDescription("Redesign the company website");
        project1.setCreatedAt(LocalDateTime.now());
        projectRepository.save(project1);

        Project project2 = new Project();
        project2.setName("Mobile App");
        project2.setDescription("Build a mobile application");
        project2.setCreatedAt(LocalDateTime.now());
        projectRepository.save(project2);

        Tag tag1 = new Tag();
        tag1.setName("bug");
        tag1.setColor("#FF0000");
        tagRepository.save(tag1);

        Tag tag2 = new Tag();
        tag2.setName("feature");
        tag2.setColor("#00FF00");
        tagRepository.save(tag2);

        Tag tag3 = new Tag();
        tag3.setName("urgent");
        tag3.setColor("#FF6600");
        tagRepository.save(tag3);

        Tag tag4 = new Tag();
        tag4.setName("backend");
        tag4.setColor("#0000FF");
        tagRepository.save(tag4);

        Tag tag5 = new Tag();
        tag5.setName("frontend");
        tag5.setColor("#9900FF");
        tagRepository.save(tag5);

        Task task1 = new Task();
        task1.setTitle("Design homepage");
        task1.setStatus(TaskStatus.TODO);
        task1.setCreatedAt(LocalDateTime.now());
        task1.setPriority(Priority.HIGH);
        task1.setProjectId(project1.getId());
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Setup database");
        task2.setStatus(TaskStatus.DONE);
        task2.setCreatedAt(LocalDateTime.now());
        task2.setPriority(Priority.CRITICAL);
        task2.setProjectId(project1.getId());
        taskRepository.save(task2);

        Task task3 = new Task();
        task3.setTitle("Create login page");
        task3.setStatus(TaskStatus.IN_PROGRESS);
        task3.setCreatedAt(LocalDateTime.now());
        task3.setPriority(Priority.HIGH);
        task3.setProjectId(project2.getId());
        taskRepository.save(task3);

        Task task4 = new Task();
        task4.setTitle("Write unit tests");
        task4.setStatus(TaskStatus.TODO);
        task4.setCreatedAt(LocalDateTime.now());
        task4.setPriority(Priority.MEDIUM);
        task4.setProjectId(project1.getId());
        taskRepository.save(task4);

        Task task5 = new Task();
        task5.setTitle("Fix navigation bug");
        task5.setStatus(TaskStatus.IN_PROGRESS);
        task5.setCreatedAt(LocalDateTime.now());
        task5.setPriority(Priority.CRITICAL);
        task5.setProjectId(project2.getId());
        taskRepository.save(task5);

        Task task6 = new Task();
        task6.setTitle("Add dark mode");
        task6.setStatus(TaskStatus.TODO);
        task6.setCreatedAt(LocalDateTime.now());
        task6.setPriority(Priority.LOW);
        task6.setProjectId(project2.getId());
        taskRepository.save(task6);

        Task task7 = new Task();
        task7.setTitle("Deploy to staging");
        task7.setStatus(TaskStatus.CANCELLED);
        task7.setCreatedAt(LocalDateTime.now());
        task7.setPriority(Priority.HIGH);
        task7.setProjectId(project1.getId());
        taskRepository.save(task7);

        Task task8 = new Task();
        task8.setTitle("Code Review");
        task8.setStatus(TaskStatus.DONE);
        task8.setCreatedAt(LocalDateTime.now());
        task8.setPriority(Priority.MEDIUM);
        task8.setProjectId(project2.getId());
        taskRepository.save(task8);

        Task task9 = new Task();
        task9.setTitle("Update dependencies");
        task9.setStatus(TaskStatus.TODO);
        task9.setCreatedAt(LocalDateTime.now());
        task9.setPriority(Priority.LOW);
        task9.setProjectId(project1.getId());
        taskRepository.save(task9);

        Task task10 = new Task();
        task10.setTitle("Performance testing");
        task10.setStatus(TaskStatus.IN_PROGRESS);
        task10.setCreatedAt(LocalDateTime.now());
        task10.setPriority(Priority.MEDIUM);
        task10.setProjectId(project2.getId());
        taskRepository.save(task10);
    }
}
