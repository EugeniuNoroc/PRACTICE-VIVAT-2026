package com.university.shelter;

import com.university.shelter.exception.AnimalNotFoundException;
import com.university.shelter.exception.DuplicateAnimalException;
import com.university.shelter.model.Animal;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REVIEW[DEBT] (с ревью 1.3): ЭТОТ КЛАСС — МЁРТВЫЙ КОД, его надо удалить.
 *
 * Main давно работает через ShelterService + DAO, а ShelterTest ты уже (молодец!)
 * перевёл на ShelterService(new InMemoryAnimalDao()). Значит Shelter больше НИКТО
 * не использует — ни прод, ни тесты. Он только:
 *   - вносит путаницу (два класса с почти одинаковыми методами),
 *   - дублирует ошибку расчёта возраста (findOlderThan/averageAge — см. InMemoryAnimalDao),
 *   - накручивает мнимое покрытие.
 *
 * Действие: git rm этот файл. Вся его логика уже живёт в InMemoryAnimalDao.
 */
public class Shelter {
    private final Map<UUID, Animal> animals;

    public Shelter() {
        this.animals = new HashMap<>();
    }

    public void accept(Animal animal) {
        if (animals.containsKey(animal.getId())) {
            throw new DuplicateAnimalException(animal.getId());
        } else {
            animals.put(animal.getId(), animal);
        }
    }

    public Optional<Animal> findById(UUID id) {
        return Optional.ofNullable(animals.get(id));
    }

    public List<Animal> findByType(Class<? extends Animal> type) {
        List<Animal> result = new ArrayList<>();
        for (Animal animal : animals.values()) {
            if (type.isInstance(animal)) {
                result.add(animal);
            }
        }
        return result;
    }

    public void release(UUID id) {
        if (animals.containsKey(id)) {
            animals.remove(id);
        } else {
            throw new AnimalNotFoundException(id);
        }
    }

    public List<Animal> findAll() {
        return new ArrayList<>(animals.values());
    }

    public List<Animal> findOlderThan(int years) {
        return animals.values()
                .stream()
                .filter(animal -> years < (LocalDate.now().getYear() - animal.getBirthDate().getYear()))
                .collect(Collectors.toList()); // Можно заменить на .toList() из Java 16+
    }

    // .max() принимает параметр Comparator который умеет сравнивать 2 эллемента, найти максимум сравнивая животных по весу
    public Optional<Animal> findHeaviest() {
        return animals.values()
                .stream()
                .max(Comparator.comparingDouble(a -> a.getWeight())); //.max((a, b) -> Double.compare(a.getWeight(), b.getWeight()))
    }

    public double averageAge() {
        return animals.values()
                .stream()
                .mapToDouble(a -> LocalDate.now().getYear() - a.getBirthDate().getYear())
                .average().orElse(0);
    }

    public Map<Class<? extends Animal>, Long> countByType() {
        return animals.values()
                .stream()
                .collect(Collectors.groupingBy(
                        a -> a.getClass(),
                        Collectors.counting()
                ));
    }
}
