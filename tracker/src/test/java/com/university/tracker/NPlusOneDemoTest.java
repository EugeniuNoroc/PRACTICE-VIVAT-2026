package com.university.tracker;

import com.university.tracker.dto.ProjectSummaryDto;
import com.university.tracker.model.Project;
import com.university.tracker.model.Task;
import com.university.tracker.repository.ProjectRepository;
import com.university.tracker.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
// по умолчанию @DataJpaTest пытается юзать встроенную БД, но в PostgreSQL у нас есть специфичные аннотации и типы, поэтому добавляем конфигурацию использования реального бд
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NPlusOneDemoTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void demonstrateNPlusOne() {
        Project project1 = new Project();
        Project project2 = new Project();
        Project project3 = new Project();

        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setProject(project1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setProject(project1);

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setProject(project2);

        Task task4 = new Task();
        task4.setTitle("Task 4");
        task4.setProject(project2);

        Task task5 = new Task();
        task5.setTitle("Task 5");
        task5.setProject(project3);

        Task task6 = new Task();
        task6.setTitle("Task 6");
        task6.setProject(project3);

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
        taskRepository.save(task5);
        taskRepository.save(task6);

        em.flush();
        em.clear();

        SessionFactory session = em.getEntityManagerFactory().unwrap(SessionFactory.class);
        Statistics stats = session.getStatistics();
        stats.clear();

        List<Project> projects = projectRepository.findAll();
        for (Project project : projects) {
            System.out.println(project.getTasks());
        }

        long queryCount = stats.getQueryExecutionCount();
        long collectionLoads = stats.getCollectionLoadCount();
        assertEquals(1, queryCount);
        assertEquals(3, collectionLoads);
    }

    @Test
    void solveWithJoinFetch() {
        Project project1 = new Project();
        Project project2 = new Project();
        Project project3 = new Project();

        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setProject(project1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setProject(project1);

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setProject(project2);

        Task task4 = new Task();
        task4.setTitle("Task 4");
        task4.setProject(project2);

        Task task5 = new Task();
        task5.setTitle("Task 5");
        task5.setProject(project3);

        Task task6 = new Task();
        task6.setTitle("Task 6");
        task6.setProject(project3);

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
        taskRepository.save(task5);
        taskRepository.save(task6);

        em.flush();
        em.clear();

        Statistics stats = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        List<Project> projects = projectRepository.findAllWithTasksAndDetails();
        for (Project project : projects) {
            System.out.println(project.getTasks());
        }

        System.out.println("Query count: " + stats.getQueryExecutionCount());
        System.out.println("Collection loads: " + stats.getCollectionLoadCount());
        System.out.println("Entity loads: " + stats.getEntityLoadCount());

        assertEquals(1, stats.getQueryExecutionCount());
    }

    @Test
    void solveWithEntityGraph() {
        Project project1 = new Project();
        Project project2 = new Project();
        Project project3 = new Project();

        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setProject(project1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setProject(project1);

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setProject(project2);

        Task task4 = new Task();
        task4.setTitle("Task 4");
        task4.setProject(project2);

        Task task5 = new Task();
        task5.setTitle("Task 5");
        task5.setProject(project3);

        Task task6 = new Task();
        task6.setTitle("Task 6");
        task6.setProject(project3);

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
        taskRepository.save(task5);
        taskRepository.save(task6);

        em.flush();
        em.clear();

        Statistics stats = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        List<Project> projects = projectRepository.findAll();

        for (Project project : projects) {
            System.out.println(project.getTasks());
        }

        System.out.println("Query count: " + stats.getQueryExecutionCount());
        System.out.println("Collection loads: " + stats.getCollectionLoadCount());

        assertEquals(1, stats.getQueryExecutionCount());
    }

    @Test
    void solveWithBatchSize() {
        Project project1 = new Project();
        Project project2 = new Project();
        Project project3 = new Project();

        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setProject(project1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setProject(project1);

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setProject(project2);

        Task task4 = new Task();
        task4.setTitle("Task 4");
        task4.setProject(project2);

        Task task5 = new Task();
        task5.setTitle("Task 5");
        task5.setProject(project3);

        Task task6 = new Task();
        task6.setTitle("Task 6");
        task6.setProject(project3);

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
        taskRepository.save(task5);
        taskRepository.save(task6);

        em.flush();
        em.clear();

        Statistics stats = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        List<Project> projects = projectRepository.findAll();

        for (Project project : projects) {
            System.out.println(project.getTasks());
        }

        System.out.println("Query count: " + stats.getQueryExecutionCount());
        System.out.println("Collection loads: " + stats.getCollectionLoadCount());

        assertEquals(1, stats.getQueryExecutionCount());
        assertEquals(3, stats.getCollectionLoadCount());
    }

    @Test
    void demonstrateDtoProjection() {
        Project project1 = new Project();
        Project project2 = new Project();
        Project project3 = new Project();

        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setProject(project1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setProject(project1);

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setProject(project2);

        Task task4 = new Task();
        task4.setTitle("Task 4");
        task4.setProject(project2);

        Task task5 = new Task();
        task5.setTitle("Task 5");
        task5.setProject(project3);

        Task task6 = new Task();
        task6.setTitle("Task 6");
        task6.setProject(project3);

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
        taskRepository.save(task5);
        taskRepository.save(task6);

        em.flush();
        em.clear();

        List<ProjectSummaryDto> projects = projectRepository.findProjectSummaries();
        ProjectSummaryDto dto = projects.stream()
                .filter(p -> p.id().equals(project1.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(3, projects.size());
        assertEquals(2, dto.taskCount());
    }
}
