package com.university.tracker;

import com.university.tracker.model.Project;
import com.university.tracker.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class TaskLifecycleTest {
    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em.createQuery("DELETE FROM Task t").executeUpdate();
        em.createQuery("DELETE FROM Project p").executeUpdate();
        em.flush();
    }

    @Test
    public void lifecycleTest(){
        // TRANSIENT
        Project project = new Project();
        project.setName("project1");
        em.persist(project);

        Task task = new Task();
        task.setTitle("Task 1");
        task.setProject(project);

        assertThat(em.contains(task)).isFalse();

        // TRANSIENT -> PERSISTENT
        em.persist(task);
        em.flush();
        assertThat(em.contains(task)).isTrue();

        // PERSISTENT -> DETACHED
        em.detach(task);
        assertThat(em.contains(task)).isFalse();

        // DETACHED -> PERSISTENT
        Task managed = em.merge(task);
        assertThat(em.contains(managed)).isTrue();

        // PERSISTENT -> REMOVED
        em.remove(managed);
        assertThat(em.contains(managed)).isFalse();
    }

    @Test
    public void dirtyCheckingTest(){
        Project project = new Project();
        project.setName("Project 1");
        em.persist(project);

        Task task = new Task();
        task.setTitle("Task 1");
        task.setProject(project);
        em.persist(task);
        em.flush();

        Task managed = em.find(Task.class, task.getId());
        managed.setTitle("Task 2");
        em.flush();
    }

    @Test
    void firstLevelCacheTest(){
        Project project = new Project();
        project.setName("Project 1");
        em.persist(project);

        Task task = new Task();
        task.setTitle("Task 1");
        task.setProject(project);
        em.persist(task);
        em.flush();
        em.clear();
        Task found1 = em.find(Task.class, task.getId());
        Task found2 = em.find(Task.class, task.getId());
        assertThat(found1).isSameAs(found2);
    }

    @Test
    void flushModeAutoTest(){
        Project project = new Project();
        project.setName("Project 1");
        em.persist(project);

        Task task = new Task();
        task.setTitle("Task 1");
        task.setProject(project);
        em.persist(task);

        Long count = em.createQuery("SELECT COUNT(t) FROM Task t", Long.class)
                .getSingleResult();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void flushModeCommitTest(){
        Project project = new Project();
        project.setName("Project 1");
        em.persist(project);

        Task task = new Task();
        task.setTitle("Task 1");
        task.setProject(project);
        em.setFlushMode(FlushModeType.COMMIT);
        em.persist(task);

        Long count = em.createQuery("SELECT COUNT(t) FROM Task t", Long.class)
                .getSingleResult();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void orphanRemovalTest(){
        Project project = new Project();
        project.setName("Project 1");
        em.persist(project);

        Task task = new Task();
        task.setTitle("Task 1");
        task.setProject(project);
        project.addTask(task);

        em.persist(project);
        em.flush();
        em.clear();

        Project foundProject = em.find(Project.class, project.getId());
        Task foundTask = em.find(Task.class, task.getId());
        foundProject.getTasks().remove(foundTask);

        em.flush();
    }
}