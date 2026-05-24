package com.university.shelter.dao;

import com.university.shelter.exception.AnimalNotFoundException;
import com.university.shelter.exception.DuplicateAnimalException;
import com.university.shelter.model.Animal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAnimalDao implements AnimalDao {
    private final Map<UUID, Animal> storage = new ConcurrentHashMap<>();

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

    @Override
    public void delete(UUID id) {
        if (storage.containsKey(id)) {
            storage.remove(id);
        } else {
            throw new AnimalNotFoundException(id);
        }
    }
}
