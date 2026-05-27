package com.university.shelter.dao;

import com.university.shelter.model.Animal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AnimalDao {
    void save(Animal animal);
    Optional<Animal> findById(UUID id);
    List<Animal> findAll();
    List<Animal> findByType(Class<? extends Animal> type);
    void update(Animal animal);
    void delete(UUID id);
    Optional<Animal> findHeaviest();
    List<Animal> findOlderThan(int years);
    double averageAge();
    Map<Class<? extends Animal>, Long> countByType();
}
