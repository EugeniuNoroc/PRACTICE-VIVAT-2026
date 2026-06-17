package com.university.tracker;

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
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            Task task = new Task();
            task.setTitle("title" + i);
            em.persist(task);
        }
        em.flush();
        long end = System.currentTimeMillis();
        System.out.println("Время: " + (end - start) + " мс");
    }

    @Test
    public void insertTaskWithStatelessSession() {
        long start = System.currentTimeMillis();

        SessionFactory sessionFactory = em.getEntityManagerFactory().unwrap(SessionFactory.class);
        try (StatelessSession statelessSession = sessionFactory.openStatelessSession()) {
            statelessSession.beginTransaction();
            for (int i = 0; i < 10000; i++) {
                Task task = new Task();
                task.setTitle("title" + i);
                statelessSession.insert(task);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Время: " + (end - start) + " мс");
    }
}
