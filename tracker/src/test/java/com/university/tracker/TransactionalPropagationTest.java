package com.university.tracker;

import com.university.tracker.model.Project;
import com.university.tracker.model.Task;
import com.university.tracker.repository.ProjectRepository;
import com.university.tracker.repository.TaskRepository;
import com.university.tracker.service.AuditService;
import com.university.tracker.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionalPropagationTest {
    @Autowired
    private TaskService taskService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    ProjectLoader projectLoader;

    @Test
    void auditFailsButTaskIsSaved() {
        Project project = projectLoader.saveProject();
        Task savedTask = taskService.create("Title", project.getId(), true);

        assertThat(savedTask).isNotNull();
        assertThat(taskRepository.findById(savedTask.getId())).isPresent();
    }

    @Test
    void required_auditFailureCascadesToParent() {
        // TODO (W3 review): тест пустой — контраст REQUIRED vs REQUIRES_NEW описан только в комментариях.
        //   Сделать вариант logAudit с REQUIRED и проверить, что при падении аудита откатывается и task
        //   (rollback-only → UnexpectedRollbackException). Чтобы откат был наблюдаем, logAudit должен писать в БД.
        // REQUIRED (дефолт): если logAudit() не ловить через try-catch —
        // исключение пометило бы транзакцию create() как rollback-only
        // и task тоже откатился бы вместе с аудитом.
        //
        // REQUIRES_NEW: аудит в отдельной транзакции.
        // Исключение поймано в create() через try-catch.
        // Транзакция create() чистая — task сохранился.
    }
}
