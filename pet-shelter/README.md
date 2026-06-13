# PRACTICE-VIVAT-2026
# Task 1 Java Basics (pet-shelter)

Учебный проект первой недели Java-практики. Приют для животных - от чистого ООП в памяти до конкурентного доступа к PostgreSQL через JDBC.

## Стек

- Java 21
- Maven
- PostgreSQL + нативный JDBC
- JUnit 5 + AssertJ
- SLF4J + Logback

## Структура проекта

```
pet-shelter/
├── docs/
│   ├── executors.md       # сравнение типов пулов потоков
│   └── isolation.md       # уровни изоляции транзакций
├── src/
│   ├── main/java/com/university/shelter/
│   │   ├── concurrency/   # sandbox для изучения многопоточности
│   │   ├── dao/           # AnimalDao, AnimalDaoJdbc, InMemoryAnimalDao, CapacityDao
│   │   ├── db/            # ConnectionFactory
│   │   ├── exception/     # иерархия исключений
│   │   ├── model/         # Animal, Dog, Cat, HealthStatus
│   │   ├── Main.java
│   │   └── ShelterService.java
│   ├── main/resources/
│   │   ├── application.properties
│   │   ├── logback.xml
│   │   └── schema.sql
│   └── test/java/com/university/shelter/
│       ├── AnimalDaoJdbcIntegrationTest.java
│       ├── CapacityDaoTest.java
│       └── ShelterTest.java
└── pom.xml
```

## Запуск

### Требования

- JDK 21
- PostgreSQL (локально)
- Maven 3.9+

### Настройка БД

```sql
CREATE DATABASE shelter_db;
CREATE USER shelter_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE shelter_db TO shelter_user;
```

Применить схему:

```bash
psql -U shelter_user -d shelter_db -f src/main/resources/schema.sql
```

### Конфигурация подключения

Создать файл `src/main/resources/application.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/shelter_db
db.username=shelter_user
db.password=your_password
```

> Файл не хранится в Git - добавлен в `.gitignore`.

### Сборка и тесты

```bash
# только unit-тесты (без БД)
mvn test

# включая integration-тесты (нужна живая БД)
mvn test -DexcludedGroups=
```

---

## Дни практики

### День 1 - ООП в памяти

Проектирование иерархии классов без БД и фреймворков.

**Что сделано:**
- Абстрактный класс `Animal` с полями `id`, `name`, `birthDate`, `weight`, `healthStatus`
- Подклассы `Dog` и `Cat` со своими специфичными полями
- Enum `HealthStatus`
- Реализованы `equals`/`hashCode` по `id`, `toString`, `Comparable<Animal>` по возрасту
- Интерфейс `Feedable` с разным поведением у собак и кошек
- Класс `Shelter` с операциями `accept`, `findById`, `findByType`, `release`
- Консольное меню в `Main`
- Валидация в конструкторах - имя не пустое, вес не отрицательный

**Главный инсайт:** разница между абстрактным классом и интерфейсом - не синтаксическая, а смысловая. Абстрактный класс - «является», интерфейс - «умеет».

---

### День 2 - Тесты, Stream API, исключения

Покрытие тестами, Stream API, собственная иерархия исключений.

**Что сделано:**
- Подключены JUnit 5 и AssertJ
- 15+ unit-тестов на `ShelterService` с паттерном AAA
- Параметризованные тесты (`@ParameterizedTest`, `@NullAndEmptySource`, `@CsvSource`)
- Stream API методы: `findOlderThan`, `countByType`, `findHeaviest`, `averageAge`
- Иерархия исключений: `ShelterException` → `AnimalNotFoundException`, `InvalidAnimalDataException`, `DuplicateAnimalException`

**Главный инсайт:** Stream - это конвейер с ленивыми вычислениями. Промежуточные операции (`filter`, `map`) ничего не делают до вызова терминальной (`collect`, `max`).

---

### День 3 - PostgreSQL + JDBC + DAO

Персистентность данных через нативный JDBC.

**Что сделано:**
- Схема БД: таблицы `animals`, `dogs`, `cats` (стратегия class table inheritance)
- `ConnectionFactory` - читает `application.properties`, отдаёт `Connection`
- Интерфейс `AnimalDao` + две реализации: `AnimalDaoJdbc` и `InMemoryAnimalDao`
- `ShelterService` принимает `AnimalDao` через конструктор (Dependency Injection вручную)
- Unit-тесты сервиса работают на `InMemoryAnimalDao` - БД не нужна
- Integration-тесты на `AnimalDaoJdbc` помечены `@Tag("integration")`
- Логирование через SLF4J + Logback

**Главный инсайт:** `ShelterService` зависит от интерфейса `AnimalDao`, а не от конкретной реализации. Можно подменить реализацию не меняя сервис - это и есть Dependency Inversion.

---

### День 4 - Многопоточность

Concurrency sandbox - от гонки данных до атомарных операций.

**Что сделано:**
- `UnsafeCounter` - демонстрация lost update в памяти: 20 потоков, результат меньше ожидаемого
- `AtomicCounter` - исправление через `AtomicInteger`
- `InMemoryAnimalDao` на `ConcurrentHashMap` - потокобезопасное хранилище
- Изучены типы пулов: `newFixedThreadPool`, `newCachedThreadPool`, `newSingleThreadExecutor`, `newScheduledThreadPool`
- `acceptManyAsync` - параллельное сохранение животных через `ExecutorService`

**Главный инсайт:** `synchronized` захватывает монитор объекта - другие потоки ждут пока он освободится.

Подробнее: [docs/executors.md](pet-shelter/docs/executors.md)

---

### День 5 - Concurrency meets persistence

Race condition на уровне БД, транзакции, уровни изоляции.

**Что сделано:**
- Таблица `shelter_capacity` - счётчик свободных мест
- `CapacityDao.tryReserveNaive()` - воспроизводит lost update: 20 потоков, бронируют больше 10 мест
- `CapacityDao.tryReserveForUpdate()` - исправление через `SELECT ... FOR UPDATE`
- Тест на 20 потоках: naive теряет обновления, FOR UPDATE - ровно 10
- `AnimalDaoJdbc.acceptBatch()` - атомарное сохранение списка животных (всё или ничего)
- Тест `acceptBatch`: 5 животных + дубликат → rollback → в БД ничего не добавилось

**Главный инсайт:** `synchronized` и `SELECT ... FOR UPDATE` - одно и то же на разных уровнях. Оба захватывают ресурс, не дают другим вмешаться, освобождают по завершении.

Подробнее: [docs/isolation.md](pet-shelter/docs/isolation.md)

---

## Модель данных

```
Animal (abstract)
├── Dog  (breed, obedienceLevel)
└── Cat  (breed, indoorOnly)
```

```sql
animals          -- общие поля всех животных
dogs             -- специфичные поля собак (animal_id FK → animals.id)
cats             -- специфичные поля кошек (animal_id FK → animals.id)
shelter_capacity -- счётчик свободных мест (день 5)
```
