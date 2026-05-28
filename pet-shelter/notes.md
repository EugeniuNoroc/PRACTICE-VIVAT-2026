# День 2. Тесты, Stream API, исключения

## 1. Иерархия исключений

В Java есть два вида исключений.

**Checked** -- наследуется от `Exception`. Компилятор заставляет обработать через `try/catch` или указать `throws` в сигнатуре метода. Используется когда ошибка ожидаема и вызывающий код обязан её обработать. Пример: файл не найден (`IOException`).

**Unchecked** -- наследуется от `RuntimeException`. Компилятор не требует обработки, падает в рантайме. Используется когда это ошибка логики программы которая не должна происходить при правильном коде. Пример: `NullPointerException`, `IllegalArgumentException`.

Своя иерархия исключений в проекте:

```
ShelterException (extends RuntimeException)
    AnimalNotFoundException (extends ShelterException)
    InvalidAnimalDataException (extends ShelterException)
    DuplicateAnimalException (extends ShelterException)
```

Все они unchecked, потому что это ошибки логики -- в нормальном сценарии они не должны происходить.

---

## 2. Optional

`Optional` создан для одной цели -- возвращаемое значение метода которое может отсутствовать.

```java
// правильно -- метод может не найти результат
public Optional<Animal> findById(UUID id) { ... }

// неправильно -- не использовать как поле класса
private Optional<String> name;

// неправильно -- не использовать как параметр метода
public void accept(Optional<Animal> animal) { ... }
```

Почему нельзя использовать как поле класса:

1. `Optional` сам может быть `null`, тогда получим `NullPointerException` на том же месте откуда пытались уйти.
2. `Optional` не реализует `Serializable`, значит объект нельзя будет сохранить (в файл, базу данных, по сети).
3. Это лишняя обёртка. Если поле обязано быть -- валидируем в конструкторе и кидаем исключение.

Правило простое:
- поле обязательно или должно быть заполнено -- валидация в конструкторе, без `Optional`
- метод может не найти результат -- возвращаем `Optional`

---

## 3. Stream API

Stream -- это конвейер операций над коллекцией.

Есть два вида операций:

**Промежуточные** (`filter`, `map`, `sorted`) -- описывают что надо сделать, но ничего не выполняют. Каждая возвращает новый `Stream`.

**Терминальные** (`collect`, `max`, `average`, `forEach`) -- запускают весь конвейер и возвращают результат.

```java
animals.values()
    .stream()
    .filter(a -> a.getWeight() > 5)      // промежуточная
    .collect(Collectors.toList());        // терминальная -- здесь всё запускается
```

### Lazy evaluation (ленивые вычисления)

Промежуточные операции не выполняются сразу. Конвейер -- это описание того что надо сделать. Реальная работа начинается только когда вызвана терминальная операция.

```java
Stream<Animal> stream = animals.values()
    .stream()
    .filter(a -> a.getWeight() > 5); // здесь ещё ничего не происходит

List<Animal> result = stream.collect(Collectors.toList()); // вот здесь запускается
```

### Примеры методов которые использовали:

```java
// найти максимум по весу
.max(Comparator.comparingDouble(a -> a.getWeight()))

// посчитать средний возраст
.mapToDouble(a -> ...).average().orElse(0)

// сгруппировать по типу и посчитать
.collect(Collectors.groupingBy(a -> a.getClass(), Collectors.counting()))
```

---

## 4. Fluent API

Fluent API -- это стиль написания кода когда методы выстраиваются в цепочку. Каждый метод возвращает тот же тип (или похожий), что позволяет продолжать цепочку.

```java
// без fluent
AbstractOptionalAssert a = assertThat(result);
a.isPresent();
a.contains(testDog);

// с fluent -- читается как предложение слева направо
assertThat(result).isPresent().contains(testDog);
```

Stream API и AssertJ построены на этом принципе.

---

## 5. Контракт equals

Любая реализация `equals` обязана соблюдать три правила:

- **Рефлексивность** -- объект равен сам себе: `a.equals(a)` всегда `true`
- **Симметричность** -- если `a.equals(b)` то `b.equals(a)`
- **Транзитивность** -- если `a.equals(b)` и `b.equals(c)` то `a.equals(c)`

Если нарушить эти правила, коллекции (`HashMap`, `ArrayList`) начнут работать непредсказуемо.

Важно: `equals` и `hashCode` всегда переопределяются вместе. Если два объекта равны по `equals`, у них должен быть одинаковый `hashCode`.

---

## 6. Long vs long

В Java есть примитивы и объекты-обёртки над ними.

```java
long x = 1L;   // примитив -- лежит прямо в памяти, быстрее
Long x = 1L;   // объект-обёртка -- лежит в куче
```

Суффикс `L` говорит компилятору что это `long`, а не `int`. Без него Java считает число `int` по умолчанию.

Коллекции (`Map`, `List`) не могут хранить примитивы, только объекты. Поэтому `Map<Class, Long>`, а не `Map<Class, long>`.

Java умеет автоматически упаковывать примитив в объект -- это называется **autoboxing**:

```java
Map<Class, Long> map = new HashMap<>();
map.put(Cat.class, 1L); // Java сама делает long -> Long
```

---

## 7. Аннотации JUnit 5

`@BeforeEach` -- метод запускается перед каждым тестом. Используется когда каждый тест должен начинать с чистого состояния и не зависеть от других.

`@BeforeAll` -- метод запускается один раз перед всеми тестами. Метод должен быть `static`. Используется для дорогой инициализации (подключение к базе данных, запуск сервера).

`@Test` -- обычный тест.

`@ParameterizedTest` -- тест который запускается несколько раз с разными входными данными.

`@DisplayName` -- задаёт читаемое название теста в отчёте, можно писать на русском.

---

## 8. Паттерн AAA в тестах

Каждый тест строится из трёх частей:

```java
@Test
void findHeaviest_shouldReturnHeaviestAnimal_ifAnimalExists() {
    // ARRANGE -- подготовка данных
    shelter.accept(testDog);
    shelter.accept(testCat);

    // ACT -- вызов метода который тестируем
    Optional<Animal> result = shelter.findHeaviest();

    // ASSERT -- проверка результата
    assertThat(result).isPresent().contains(testDog);
}
```

Одно правило: один тест -- одна мысль. Не надо проверять несколько несвязанных вещей в одном тесте.

Именование методов: `methodName_shouldDoSomething_whenCondition`.

---

## 9. Шпаргалка по AssertJ

```java
assertThat(optional).isPresent()                      // Optional не пустой
assertThat(optional).isEmpty()                        // Optional пустой
assertThat(optional).contains(object)                 // Optional содержит объект

assertThat(list).hasSize(1)                           // размер коллекции
assertThat(list).contains(object)                     // коллекция содержит элемент
assertThat(list).doesNotContain(object)               // коллекция не содержит элемент
assertThat(list).allMatch(a -> a instanceof Cat)      // все элементы соответствуют условию
assertThat(list).isNotEmpty()                         // коллекция не пустая

assertThat(map).containsEntry(Cat.class, 1L)          // Map содержит пару ключ-значение

assertThat(value).isGreaterThan(6)                    // число больше чем
assertThat(value).isLessThan(10)                      // число меньше чем

assertThatThrownBy(() -> ...).isInstanceOf(MyException.class) // метод кидает исключение
```

## 10. Executors  
#### newFixedThreadPool(n)
Фиксированное количество потоков. Лишние задачи ждут в очереди. Предсказуемое потребление ресурсов.

#### newCachedThreadPool() 
Поздаёт новый поток для каждой задачи если нет свободных. Неиспользуемые потоки удаляются через 60 секунд. Хорош для коротких задач, опасен при большой нагрузке — может создать тысячи потоков.

#### newSingleThreadExecutor()
Один поток, все задачи выполняются последовательно. Гарантирует порядок выполнения.

#### newScheduledThreadPool(n)
Позволяет запускать задачи по расписанию или с задержкой. Аналог cron.

#### Virtual Threads (Java 21)
Лёгкие потоки управляемые JVM, не ОС. Можно создать миллионы. Идеальны для IO-bound задач. Не использую в этом таске, но стоит знать.

### Разница:
#### newFixedThreadPool(n)  
Хорош для "не экстренных" задач так как лишние задачи будут ждать в очереди что предоставляет предсказуемое потребление ресурсов

#### newCachedThreadPool()  
Хорош для коротких задач, но непредскаезуем и может загрузить систему создав тысячи потоков

#### newSingeThreadExecutor()  
Гарантированный порядок выполнения задач, но самый медлительный так как предоставляет один поток

#### newSchedulerThreadPool(n)  
Хорош если таски, которые необходимо выполнить, выставлены в виде расписания или с задержкой, иначе непредсказуемость

#### Virtual Threads (Java 21)  
Легкие и удобные потоки, которые не нагружают OC, хороший вариант для IO-bound задач (запросы к БД, HTTP вызовы, файлы). Там поток большую часть времени просто ждёт - Virtual Thread в это время освобождается и берёт другую задачу. Можно создать миллионы таких потоков без нагрузки на ОС.