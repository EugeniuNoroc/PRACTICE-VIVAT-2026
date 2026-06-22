package com.university.tracker;

import com.university.tracker.model.Project;
import com.university.tracker.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class OptimisticLockingTest {

    @Autowired
    private ProjectLoader projectLoader;

    @Test
    public void optimisticLockingTest() {
        Project project = projectLoader.saveProject();
        UUID taskId = projectLoader.saveTask(project.getId()).getId();

        Task firstView = projectLoader.loadTask(taskId); // version = 0
        Task secondView = projectLoader.loadTask(taskId); // version = 0

        projectLoader.updateTaskEntity(firstView, "First update");

        assertThatThrownBy(() -> projectLoader.updateTaskEntity(secondView, "Second update")).isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    public void pessimisticLockingTest() {
        Project project = projectLoader.saveProject();
        Task task = projectLoader.saveTask(project.getId());

        Task lockedTask = projectLoader.loadTaskWithLock(task.getId());

        assertThat(lockedTask).isNotNull();
        assertThat(lockedTask.getId()).isEqualTo(task.getId());
    }
}
