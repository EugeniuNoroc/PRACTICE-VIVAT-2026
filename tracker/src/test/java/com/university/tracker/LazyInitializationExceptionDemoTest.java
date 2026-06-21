package com.university.tracker;

import com.university.tracker.model.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class LazyInitializationExceptionDemoTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProjectLoader projectLoader;

    @Test
    public void testLazyInitialization() {
        UUID id = projectLoader.saveProjectWithTasks();
        Project loadedProject =  projectLoader.loadProject(id);
        assertThatThrownBy(() -> loadedProject.getTasks().size()).isInstanceOf(LazyInitializationException.class);
    }
}
