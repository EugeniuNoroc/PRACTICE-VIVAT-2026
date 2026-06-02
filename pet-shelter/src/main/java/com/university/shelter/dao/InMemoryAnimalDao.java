package com.university.shelter.dao;

import com.university.shelter.exception.AnimalNotFoundException;
import com.university.shelter.exception.DuplicateAnimalException;
import com.university.shelter.model.Animal;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryAnimalDao implements AnimalDao {
    private final Map<UUID, Animal> storage = new ConcurrentHashMap<>();
    // private final Map<UUID, Animal> storage = new HashMap<>();

    @Override
    public void save(Animal animal) {
        if (storage.putIfAbsent(animal.getId(), animal) != null) {
            throw new DuplicateAnimalException(animal.getId());
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

    @Override
    public List<Animal> findOlderThan(int years) {
        return storage.values()
                .stream()
                .filter(animal -> years < (ChronoUnit.YEARS.between(animal.getBirthDate(), LocalDate.now()))).toList();
    }

    @Override
    public double averageAge() {
        return storage.values()
                .stream()
                .mapToDouble(a -> ChronoUnit.YEARS.between(a.getBirthDate(), LocalDate.now()))
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
