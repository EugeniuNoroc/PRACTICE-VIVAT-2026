# PRACTICE-VIVAT-2026

Структурированная Java-практика под руководством куратора Алексея Борискo.
Три недели нарастающей сложности - от чистого ООП до полноценного Spring приложения.

## Структура репозитория

```
PRACTICE-VIVAT-2026/
├── pet-shelter/       # Week 1 - Java Core, JDBC, многопоточность
├── library-catalog/   # Week 2 - Spring Boot, REST, JdbcTemplate
└── tracker/           # Week 3 - Spring Data JPA, Hibernate, транзакции
```

---

## Week 1 - pet-shelter

**Тема:** Java Core, PostgreSQL + нативный JDBC, многопоточность

**Стек:** Java 21, Maven, PostgreSQL, JUnit 5, AssertJ, SLF4J

| День | Тема | Ключевые концепции |
|------|------|--------------------|
| 1 | ООП в памяти | abstract class, interface, enum, equals/hashCode, Comparable |
| 2 | Тесты и Stream API | JUnit 5, AssertJ, параметризованные тесты, Stream API, иерархия исключений |
| 3 | PostgreSQL + JDBC | DAO паттерн, ConnectionFactory, интеграционные тесты |
| 4 | Многопоточность | ExecutorService, AtomicInteger, race conditions, ConcurrentHashMap, CompletableFuture |
| 5 | Конкурентность + БД | SELECT FOR UPDATE, уровни изоляции, batch операции |

**Главный инсайт недели:** `synchronized` в памяти и `SELECT FOR UPDATE` в БД - одна концепция на разных уровнях. Оба захватывают ресурс и не дают другим вмешаться.

[Подробнее →](pet-shelter/README.md)

---

## Week 2 - library-catalog

**Тема:** Spring Boot, REST API, Bean Validation, тестирование

**Стек:** Java 21, Spring Boot 4.0.6, JdbcTemplate, PostgreSQL, springdoc-openapi, Mockito

| День | Тема | Ключевые концепции |
|------|------|--------------------|
| 1 | Spring Boot bootstrap | IoC/DI, @Service/@Repository, application.yml, профили |
| 2 | Spring JDBC | JdbcTemplate, RowMapper, NamedParameterJdbcTemplate, HikariCP |
| 3 | REST API | @RestController, DTO, маппинг, пагинация, бизнес-логика |
| 4 | Валидация и документация | Bean Validation, @ValidIsbn, @RestControllerAdvice, Swagger/OpenAPI |
| 5 | Тестирование | Mockito, @WebMvcTest, @MockitoBean, ArgumentCaptor |

**Главный инсайт недели:** Ручной DI из Week 1 (`new BookService(new BookRepository())`) - это то, что Spring делает автоматически через Application Context. Один `GlobalExceptionHandler` заменяет десятки try-catch по всему коду.

[Подробнее →](library-catalog/README.md)

---

## Week 3 - tracker

**Тема:** Spring Data JPA, Hibernate ORM, транзакции, блокировки, Flyway

**Стек:** Java 21, Spring Boot 4.1.0, Hibernate 7.4.1, PostgreSQL, Flyway 12, JUnit 6

| День | Тема | Ключевые концепции |
|------|------|--------------------|
| 1 | Spring Data JPA | JpaRepository, автогенерация запросов, сравнение с JdbcTemplate |
| 2 | Hibernate напрямую | EntityManager, persistence context, dirty checking, flush modes, StatelessSession |
| 3 | Relationships | @OneToMany, @ManyToMany, @MapsId, cascade, orphanRemoval, owning/inverse side |
| 4 | N+1 и fetching | JOIN FETCH, @EntityGraph, @BatchSize, LazyInitializationException, OSIV, DTO projection |
| 5 | Транзакции и блокировки | @Transactional propagation, AOP proxy, @Version, @Lock, Flyway миграции |

**Главный инсайт недели:** Все три недели - одна идея в разных декорациях. `AtomicInteger.compareAndSet` (W1) = `@Version` (W3). `SELECT FOR UPDATE` (W1) = `@Lock(PESSIMISTIC_WRITE)` (W3). Абстракции меняются, концепции остаются.

[Подробнее →](tracker/README.md)

---

## Сквозные темы

### Эволюция работы с БД

```
Week 1: connection.prepareStatement("INSERT INTO ...") - всё вручную
Week 2: jdbcTemplate.update("INSERT INTO ...", params) - без boilerplate
Week 3: taskRepository.save(task) - Hibernate генерирует SQL сам
```

### Эволюция тестирования

```
Week 1: InMemoryAnimalDao вручную для изоляции тестов
Week 2: @Mock / @MockitoBean - автоматические заглушки
Week 3: @DataJpaTest / @SpringBootTest - slice и интеграционные тесты
```

### Эволюция обработки ошибок

```
Week 1: try-catch в каждом методе, ручной rollback
Week 2: GlobalExceptionHandler - централизованно
Week 3: @Transactional - автоматический rollback при исключении
```

### Эволюция конкурентности

```
Week 1: synchronized / AtomicInteger в памяти
Week 1: SELECT FOR UPDATE на уровне БД
Week 3: @Version (optimistic) - проверка при коммите
Week 3: @Lock(PESSIMISTIC_WRITE) - SELECT FOR UPDATE через Hibernate
```

---

## Запуск проектов

Каждый проект запускается независимо. Требования и инструкции - в README каждого модуля.

Общие требования:
- JDK 21
- PostgreSQL (локально)
- Maven 3.9+
