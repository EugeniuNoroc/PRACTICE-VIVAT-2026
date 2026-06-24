package com.university.tracker;

import com.university.tracker.model.Project;
import com.university.tracker.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class BulkInsertTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    @Transactional
    public void insertTaskWithPersistenceContext() {
        Project project = new Project();
        em.persist(project);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            Task task = new Task();
            task.setTitle("title" + i);
            task.setProject(project);
            em.persist(task);
        }
        em.flush();
        long end = System.currentTimeMillis();
        System.out.println("Время: " + (end - start) + " мс");
    }

    @Test
    public void insertTaskWithStatelessSession() {
        // TODO (W3 review): транзакция StatelessSession не коммитится (beginTransaction есть, commit нет) →
        //   на close всё откатывается, бенчмарк меряет почти no-op. И hibernate.jdbc.batch_size нигде не задан,
        //   поэтому честного ускорения нет (замер: ~2734 vs ~2784 мс). Добавить commit + batch_size + ассерты.
        long start = System.currentTimeMillis();

        SessionFactory sessionFactory = em.getEntityManagerFactory().unwrap(SessionFactory.class);
        try (StatelessSession statelessSession = sessionFactory.openStatelessSession()) {
            statelessSession.beginTransaction();

            Project project = new Project();
            statelessSession.insert(project);

            for (int i = 0; i < 10000; i++) {
                Task task = new Task();
                task.setTitle("title" + i);
                task.setProject(project);
                statelessSession.insert(task);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Время: " + (end - start) + " мс");
    }
}
