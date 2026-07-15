# PRACTICE-VIVAT-2026
# Task 2 Spring Boot (library-catalog)

Учебный проект второй недели Java-практики. Каталог библиотеки - от Spring Boot bootstrap до REST API с валидацией, документацией и тестами.

## Стек

- Java 21
- Spring Boot 4.0.6
- Maven
- PostgreSQL + Spring JdbcTemplate
- Bean Validation (Jakarta)
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito + AssertJ

## Структура проекта

```
library-catalog/
├── docs/
│   ├── di.md                    # три вида инжекции зависимостей
│   ├── error-handling.md        # сравнение обработки ошибок W1 vs W2
│   ├── mock-vs-mockbean.md      # разница @Mock и @MockitoBean
│   └── spring-jdbc-vs-jdbc.md  # сравнение JdbcTemplate с голым JDBC
├── src/
│   ├── main/java/com/university/library/
│   │   ├── config/              # LibraryConfig (@Bean Clock)
│   │   ├── controller/          # REST контроллеры, мапперы, валидаторы
│   │   │   └── dto/             # DTO - Request/Response классы
│   │   ├── demo/                # InjectionDemo (учебный пример DI)
│   │   ├── exception/           # иерархия исключений
│   │   ├── model/               # Author, Book, Reader, Loan
│   │   ├── repository/          # JdbcTemplate репозитории
│   │   ├── service/             # бизнес-логика
│   │   ├── DataInitializer.java # CommandLineRunner - вывод при старте
│   │   └── LibraryCatalogApplication.java
│   ├── main/resources/
│   │   ├── application.yaml
│   │   ├── application-dev.yml
│   │   ├── application-test.yml
│   │   ├── db/schema.sql
│   │   └── requests.http        # примеры HTTP запросов
│   └── test/java/com/university/library/test/
│       ├── BookServiceTest.java
│       ├── AuthorServiceTest.java
│       ├── ReaderServiceTest.java
│       ├── LoanServiceTest.java
│       ├── BookControllerTest.java
│       ├── AuthorControllerTest.java
│       ├── ReaderControllerTest.java
│       └── LoanControllerTest.java
└── pom.xml
```

## Запуск

### Требования

- JDK 21
- PostgreSQL
- Maven 3.9+

### Настройка БД

```sql
CREATE DATABASE library_db;
CREATE USER library_user WITH PASSWORD 'library_pass';
GRANT ALL PRIVILEGES ON DATABASE library_db TO library_user;
GRANT ALL ON SCHEMA public TO library_user;
```

Применить схему:

```bash
psql -h localhost -U library_user -d library_db -f src/main/resources/db/schema.sql
```

### Конфигурация

Файл `src/main/resources/application.yaml` уже содержит настройки для профиля `dev`. Пароль БД задаётся через переменные окружения или дефолтное значение в `application-dev.yml`.

### Сборка и запуск

```bash
# Запустить приложение
mvn spring-boot:run

# Только unit-тесты (без БД)
mvn test

# Swagger UI
http://localhost:8080/swagger-ui/index.html
```

---

## Дни практики

### День 1 - Bootstrap Spring Boot, IoC/DI

Создание проекта, понимание инверсии управления и внедрения зависимостей.

**Что сделано:**
- Проект через Spring Initializr (Web, JDBC, Validation, DevTools)
- Пакеты по слоям: `model`, `repository`, `service`, `controller`, `config`, `exception`
- 4 модели: `Author`, `Book`, `Reader`, `Loan`
- `BookRepository` как заглушка, `BookService` с constructor injection
- `LibraryConfig` - `@Configuration` с `@Bean Clock`
- `application.yml` с профилями `dev` / `test`
- `DataInitializer` - `CommandLineRunner` выводит количество книг при старте
- `InjectionDemo` - демонстрация field / setter / constructor injection
- `docs/di.md` - три вида инжекции своими словами

**Главный инсайт:** `AnimalDao` в конструкторе `ShelterService` из W1 - это ручной DI. Spring делает то же самое автоматически через `@Service` / `@Repository` и Application Context.

---

### День 2 - Spring JDBC и JdbcTemplate

Замена голого JDBC на `JdbcTemplate`. Сравнение boilerplate.

**Что сделано:**
- `BookRepository`, `AuthorRepository`, `ReaderRepository`, `LoanRepository` на `JdbcTemplate`
- `RowMapper` для каждой модели
- `NamedParameterJdbcTemplate` - два метода с именованными параметрами
- JOIN запрос `findAllBooksWithAuthor` → `List<BookResponse>`
- `countBooksByAuthor` через `ResultSetExtractor` → `Map<UUID, Integer>`
- Пагинация: `findAll(int offset, int limit)` + `count()`
- HikariCP настроен в `application.yml`
- Логирование SQL через `org.springframework.jdbc.core: DEBUG`
- `docs/spring-jdbc-vs-jdbc.md` - сравнение с W1

**Главный инсайт:** `JdbcTemplate` убирает открытие/закрытие Connection, try-with-resources на PreparedStatement/ResultSet, перехват SQLException. SQL и RowMapper остаются твоей задачей.

---

### День 3 - REST API, DTO, пагинация

Первый HTTP слой - контроллеры, DTO, маппинг, бизнес-логика выдачи книг.

**Что сделано:**
- `BookController`, `AuthorController`, `ReaderController` - полный CRUD
- `LoanController` - выдача книги с бизнес-проверками
- DTO: `*CreateRequest`, `*UpdateRequest`, `*Response` как Java records
- `BookMapper`, `AuthorMapper`, `ReaderMapper` для конвертации Entity ↔ DTO
- `PagedResponse<T>` - пагинированный ответ с метаданными
- `GET /api/books` возвращает `PagedResponse<BookResponse>` с `authorName` через JOIN
- `LoanService.issueLoan` - проверяет наличие книги, читателя, свободных копий
- 409 Conflict при отсутствии копий
- `requests.http` - примеры всех endpoints

**Главный инсайт:** Контроллер - переводчик между HTTP и Java. Сервис не знает про HTTP. Репозиторий не знает про бизнес-логику. Каждый слой знает только о следующем.

---

### День 4 - Bean Validation, @ControllerAdvice, Swagger

Дружелюбный API - валидация входных данных, централизованная обработка ошибок, документация.

**Что сделано:**
- Bean Validation на DTO: `@NotBlank`, `@Email`, `@Min`, `@Max`, `@PastOrPresent`, `@Size`
- Кастомный constraint `@ValidIsbn` + `IsbnValidator` с алгоритмом проверки ISBN-10/13
- `@Valid` на `@RequestBody` в контроллерах
- `GlobalExceptionHandler` (`@RestControllerAdvice`) - централизованная обработка:
  - `MethodArgumentNotValidException` → 400 с `ApiError`
  - `EntityNotFoundException` → 404
  - `NoCopiesAvailableException` → 409
  - `Exception` → 500 без stack trace
- `ApiError` - единый формат ошибки с полями `timestamp`, `status`, `error`, `path`, `violations`
- Иерархия исключений: `LibraryException` → `EntityNotFoundException` → `BookNotFoundException`
- Swagger UI через `springdoc-openapi`, `@Operation` и `@ApiResponse` на всех endpoints
- `@Schema` на полях DTO
- `docs/error-handling.md` - сравнение с W1

**Главный инсайт:** В W1 каждый метод оборачивал SQLException в try-catch вручную. В W2 один `GlobalExceptionHandler` ловит всё централизованно и возвращает чистый JSON с правильным статус кодом.

---

### День 5 - Mockito, @WebMvcTest, тестирование

Покрытие кода unit-тестами с Mockito и slice-тестами с WebMvcTest.

**Что сделано:**
- `BookServiceTest`, `AuthorServiceTest`, `ReaderServiceTest` - unit тесты сервисов с `@Mock` + `@InjectMocks`
- `LoanServiceTest` - тесты бизнес-логики:
  - `NoCopiesAvailableException` при отсутствии копий
  - `ArgumentCaptor` для проверки что `copiesAvailable` уменьшился
- `BookControllerTest`, `AuthorControllerTest`, `ReaderControllerTest`, `LoanControllerTest` - slice тесты с `@WebMvcTest` + `RestTestClient`
- Тест `@ControllerAdvice` - сервис бросает исключение → правильный статус код
- Тест валидации - невалидный body → 400
- `docs/mock-vs-mockbean.md`
- Итого: 19 тестов, все зелёные

**Главный инсайт:** В W1 для тестирования сервиса нужно было писать `InMemoryAnimalDao` вручную. Mockito генерирует объект-заглушку автоматически. `@Mock` - без Spring, `@MockitoBean` - когда Spring контекст запущен.

---

## Модель данных

```
Author (id, fullName, birthYear, biography)
Book   (id, title, isbn, year, authorId, copiesTotal, copiesAvailable)
Reader (id, fullName, email, registrationDate)
Loan   (id, bookId, readerId, loanDate, returnDate, returned)
```

```sql
authors   -- авторы книг
books     -- книги с ссылкой на автора
readers   -- читатели библиотеки
loans     -- записи о выдаче книг
```

## API endpoints

| Метод | URL | Описание |
|-------|-----|----------|
| GET | /api/books?page=0&size=10 | Все книги с пагинацией |
| GET | /api/books/{id} | Книга по ID |
| POST | /api/books | Создать книгу |
| PUT | /api/books/{id} | Обновить книгу |
| DELETE | /api/books/{id} | Удалить книгу |
| GET | /api/authors | Все авторы |
| GET | /api/authors/{id} | Автор по ID |
| POST | /api/authors | Создать автора |
| PUT | /api/authors/{id} | Обновить автора |
| DELETE | /api/authors/{id} | Удалить автора |
| GET | /api/readers | Все читатели |
| GET | /api/readers/{id} | Читатель по ID |
| POST | /api/readers | Создать читателя |
| PUT | /api/readers/{id} | Обновить читателя |
| DELETE | /api/readers/{id} | Удалить читателя |
| POST | /api/loans?bookId=...&readerId=... | Выдать книгу |
