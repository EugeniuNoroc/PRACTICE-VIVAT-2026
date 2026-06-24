# PRACTICE-VIVAT-2026
# Task 3 Spring Data JPA / Hibernate (task tracker)

Учебный проект третьей недели Java-практики. Task Tracker - углублённое изучение
Spring Data JPA, Hibernate ORM, транзакций и блокировок на реальной базе данных.

## Стек

- Java 21
- Spring Boot 4.1.0
- Spring Data JPA + Hibernate 7.4.1
- PostgreSQL 18
- Flyway 12
- JUnit 6 + AssertJ
- HikariCP

## Структура проекта

```
tracker/
├── docs/
│   ├── aop-proxy-gotcha.md          # AOP подводный камень @Transactional
│   ├── bulk-insert-comparison.md    # сравнение стратегий bulk insert
│   ├── dirty-checking.md            # как работает dirty checking
│   ├── entity-lifecycle.md          # жизненный цикл entity
│   ├── n-plus-one.md                # N+1 проблема и решения
│   ├── OptimisticLockException.md   # стратегии обработки optimistic lock
│   ├── persistence-context.md       # persistence context
│   └── spring-data-vs-spring-jdbc.md
├── src/
│   ├── main/java/com/university/tracker/
│   │   ├── controller/              # TaskController - REST endpoint
│   │   ├── dto/                     # ProjectSummaryDto - DTO projection
│   │   ├── model/                   # Task, Project, User, Tag, TaskTag, TaskDescriptionDetails
│   │   │   └── enums/               # Priority, TaskStatus
│   │   ├── repository/              # JpaRepository интерфейсы
│   │   ├── runner/                  # DataInitializer - seed данные при старте
│   │   ├── service/                 # TaskService, AuditService, TaskNativeService
│   │   └── TrackerApplication.java
│   ├── main/resources/
│   │   ├── db/migration/
│   │   │   ├── V1__init_schema.sql  # базовая схема
│   │   │   ├── V2__add_task_version.sql  # optimistic locking колонка
│   │   │   └── V3__add_task_tags.sql     # many-to-many таблица
│   │   └── application.yml
│   └── test/java/com/university/tracker/
│       ├── ProjectLoader.java                   # хелпер для тестов
│       ├── BulkInsertTest.java
│       ├── TaskLifecycleTest.java               # entity lifecycle + dirty checking
│       ├── NPlusOneDemoTest.java                # N+1 демо + решения
│       ├── LazyInitializationExceptionDemoTest.java
│       ├── TransactionalPropagationTest.java    # REQUIRED vs REQUIRES_NEW
│       └── OptimisticLockingTest.java           # @Version + @Lock
└── pom.xml
```

## Запуск

### Требования

- JDK 21
- PostgreSQL (локально)
- Maven 3.9+

### Настройка БД

```sql
CREATE DATABASE tracker_db;
CREATE USER tracker_user WITH PASSWORD 'tracker_pass';
GRANT ALL PRIVILEGES ON DATABASE tracker_db TO tracker_user;
```

### Конфигурация

Переменные окружения для основного профиля:

```bash
export DB_USER=tracker_user
export DB_PASSWORD=tracker_pass
```

Тестовый профиль (`application-test.yml`) использует захардкоженные значения.

### Сборка и тесты

```bash
# запуск тестов
mvn test

# запуск приложения
DB_USER=tracker_user DB_PASSWORD=tracker_pass mvn spring-boot:run
```

Flyway применит все миграции автоматически при старте.

---

## Дни практики

### День 1 - Spring Data JPA, первое знакомство

Запуск Task Tracker на Spring Data JPA. Сравнение с JdbcTemplate из Week 2.

**Что сделано:**
- Четыре entity: `Task`, `Project`, `User`, `Tag`
- `JpaRepository` интерфейсы без реализаций
- REST endpoint `TaskController`
- `DataInitializer` - seed данные при старте

**Главный инсайт:** `interface TaskRepository extends JpaRepository<Task, UUID>` - одна строка заменяет весь DAO из Week 2. Spring Data генерирует реализацию в runtime.

---

### День 2 - Hibernate напрямую: Session и Persistence Context

Работа с Hibernate ниже уровня Spring Data.

**Что сделано:**
- `EntityManager` напрямую: `persist`, `merge`, `detach`, `find`
- Entity lifecycle тест: Transient → Persistent → Detached → Removed
- Dirty checking демо: изменение поля без явного `save()` → автоматический UPDATE
- First-level cache: два `findById` на один id → один SQL запрос
- FlushMode: AUTO vs COMMIT - когда Hibernate синхронизирует с БД
- `StatelessSession` бенчмарк для bulk insert

**Главный инсайт:** Persistence context - это кэш первого уровня + трекер изменений.
Hibernate при flush сравнивает текущее состояние объекта со снимком при загрузке
и генерирует UPDATE автоматически.

Подробнее: docs/persistence-context.md, docs/dirty-checking.md

---

### День 3 - Relationships: @OneToMany, @ManyToMany, cascade

Связи между entity, owning/inverse side, cascade стратегии.

**Что сделано:**
- Двунаправленные связи `@ManyToOne` / `@OneToMany` между `Task` и `Project`
- `@OneToOne` с `@MapsId` между `Task` и `TaskDescriptionDetails`
- Явная join entity `TaskTag` с `@EmbeddedId` для `@ManyToMany`
- `CascadeType` стратегии и `orphanRemoval`
- `equals`/`hashCode` через `getClass()` для корректной работы в коллекциях

**Главный инсайт:** owning side определяется по `@JoinColumn` - у кого FK, тот владелец.
`mappedBy` означает "FK не у меня, смотри на то поле". Hibernate синхронизирует
связь только через owning side.

---

### День 4 - N+1 проблема и fetching strategies

Воспроизведение и решение N+1 problem.

**Что сделано:**
- N+1 демо через `projectRepository.findAll()` + цикл по lazy коллекциям
- Решение 1: `JOIN FETCH` в JPQL - один запрос вместо N+1
- Решение 2: `@EntityGraph` - декларативно, без JPQL
- Решение 3: `@BatchSize` - группировка lazy-загрузок через `WHERE id IN (...)`
- `LazyInitializationException` - воспроизведение и обработка
- OSIV (`open-in-view: false`) - почему выключаем
- DTO projection через Java record и interface projection
<!-- TODO (W3 review): interface projection в коде нет — реализована только record-проекция (ProjectSummaryDto).
     Убрать упоминание interface projection здесь, либо добавить интерфейс-проекцию в ProjectRepository. -->

**Главный инсайт:** N+1 - естественное следствие lazy loading. JOIN FETCH и @EntityGraph
превращают ленивую загрузку в жадную для конкретного запроса. @BatchSize группирует
lazy-загрузки. DTO projection убирает overhead persistence context для read-only сценариев.

Подробнее: docs/n-plus-one.md

---

### День 5 - @Transactional, locking, Flyway

Транзакции, блокировки, версионированные миграции.

**Что сделано:**
- `TaskService` с `@Transactional`, `@Transactional(readOnly = true)`
- `AuditService` с `@Transactional(propagation = REQUIRES_NEW)`
- Демо REQUIRED vs REQUIRES_NEW: аудит падает - task сохраняется
- AOP подводный камень: internal call обходит прокси, `@Transactional` не работает
- Optimistic locking через `@Version` - `ObjectOptimisticLockingFailureException` при конкурентном обновлении
- Pessimistic locking через `@Lock(PESSIMISTIC_WRITE)` - генерирует `SELECT ... FOR UPDATE`
- Flyway: три версионированные миграции, `ddl-auto: validate`

**Главный инсайт:** `@Lock(PESSIMISTIC_WRITE)` - это тот же `SELECT ... FOR UPDATE`
из Week 1, только Hibernate генерирует его автоматически. `@Version` (optimistic) -
аналог `AtomicInteger.compareAndSet` из Week 1, но на уровне БД.

Подробнее: docs/aop-proxy-gotcha.md, docs/OptimisticLockException.md

---

## Модель данных

```
Project (1) ──── (N) Task
                      │
                      ├── (1) TaskDescriptionDetails  (@MapsId)
                      │
                      └── (N) TaskTag (N) ──── Tag

User (1) ──── (N) Task  (assignee)
```

```sql
projects                 -- проекты
tasks                    -- задачи (FK: project_id, assignee_id, version для optimistic lock)
users                    -- пользователи
tags                     -- теги
task_tags                -- many-to-many tasks ↔ tags
task_description_details -- детали задачи (PK = FK = task_id)
flyway_schema_history    -- история Flyway миграций
```
