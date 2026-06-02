package com.university.shelter.dao;

import com.university.shelter.exception.AnimalNotFoundException;
import com.university.shelter.exception.DuplicateAnimalException;
import com.university.shelter.model.Animal;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryAnimalDao implements AnimalDao {
    // REVIEW[GOOD] (день 4): ConcurrentHashMap выбран осознанно — эта DAO ходит под
    // нагрузкой потоков в acceptManyAsync. Закомментированный HashMap оставь только
    // как учебную заметку (или вынеси в notes), в рабочем коде такие хвосты не держат.
    private final Map<UUID, Animal> storage = new ConcurrentHashMap<>();
    // private final Map<UUID, Animal> storage = new HashMap<>();

    /**
     * REVIEW[BUG] — ГЛАВНЫЙ УЧЕБНЫЙ МОМЕНТ ДНЯ 4. RACE CONDITION В ТВОЁМ КОДЕ.
     *
     * containsKey(...) + put(...) — это НЕ атомарная операция (паттерн TOCTOU:
     * time-of-check / time-of-use). ConcurrentHashMap делает атомарным каждый ОТДЕЛЬНЫЙ
     * вызов, но НЕ их комбинацию. Два потока могут одновременно увидеть
     * containsKey == false и оба сделать put — дубликат не отловится, один перезапишет другого.
     *
     * Это РОВНО та же гонка, что ты сегодня ловил в UnsafeCounter (там value++ — три
     * операции; здесь check+put — две). И эта DAO работает в acceptManyAsync МНОГОПОТОЧНО.
     *
     * Фикс — атомарный putIfAbsent (одна операция вместо двух):
     *   if (storage.putIfAbsent(animal.getId(), animal) != null)
     *       throw new DuplicateAnimalException(animal.getId());
     *
     * REVIEW[THINK]: напиши стресс-тест — 100 потоков сохраняют животных с ОДНИМ id.
     * Сколько дубликатов "проскочит" до фикса? А после? (как в RaceConditionStressTest)
     */
    @Override
    public void save(Animal animal) {
        if (storage.containsKey(animal.getId())) {
            throw new DuplicateAnimalException(animal.getId());
        } else {
            storage.put(animal.getId(), animal);
        }
    }

    @Override
    public Optional<Animal> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Animal> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Animal> findByType(Class<? extends Animal> type) {
        List<Animal> result = new ArrayList<>();
        for (Animal animal : storage.values()) {
            if (type.isInstance(animal)) {
                result.add(animal);
            }
        }
        return result;
    }

    @Override
    public void update(Animal animal) {
        if (!storage.containsKey(animal.getId())) {
            throw new AnimalNotFoundException(animal.getId());
        }
        storage.put(animal.getId(), animal);
    }

    // REVIEW[NIT] (день 4): тот же TOCTOU, что и в save (см. выше), плюс два прохода
    // по map. remove(id) возвращает предыдущее значение, поэтому короче и атомарно:
    //   if (storage.remove(id) == null) throw new AnimalNotFoundException(id);
    @Override
    public void delete(UUID id) {
        if (storage.containsKey(id)) {
            storage.remove(id);
        } else {
            throw new AnimalNotFoundException(id);
        }
    }

    @Override
    public Optional<Animal> findHeaviest() {
        return storage.values()
                .stream()
                .max(Comparator.comparingDouble(a -> a.getWeight())); //.max((a, b) -> Double.compare(a.getWeight(), b.getWeight()))
    }

    // REVIEW[DEBT] (с ревью 1.2, всё ещё не исправлено): возраст считается как разница
    // ГОДОВ (now.getYear() - birth.getYear()) без учёта месяца/дня. Животное, рождённое
    // 2025-12-31, в январе 2026 получит "возраст 1". Используй:
    //   ChronoUnit.YEARS.between(animal.getBirthDate(), LocalDate.now())  // = полных лет
    // Та же ошибка продублирована в Shelter.java (мёртвый класс — см. там, его надо удалить).
    // REVIEW[NIT]: ты сам пометил "Можно заменить на .toList()" — так замени, раз знаешь.
    @Override
    public List<Animal> findOlderThan(int years) {
        return storage.values()
                .stream()
                .filter(animal -> years < (LocalDate.now().getYear() - animal.getBirthDate().getYear()))
                .collect(Collectors.toList()); // Можно заменить на .toList() из Java 16+
    }

    @Override
    public double averageAge() {
        return storage.values()
                .stream()
                .mapToDouble(a -> LocalDate.now().getYear() - a.getBirthDate().getYear())
                .average().orElse(0);
    }

    @Override
    public Map<Class<? extends Animal>, Long> countByType() {
        return storage.values()
                .stream()
                .collect(Collectors.groupingBy(
                        a -> a.getClass(),
                        Collectors.counting()
                ));
    }
}
