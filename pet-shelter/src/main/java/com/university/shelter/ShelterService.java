package com.university.shelter;

import com.university.shelter.dao.AnimalDao;
import com.university.shelter.model.Animal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ShelterService {
    private final AnimalDao dao;

    public ShelterService(AnimalDao dao) {
        this.dao = dao;
    }

    public void accept(Animal animal) {
        dao.save(animal);
    }

    public Optional<Animal> findById(UUID id) {
        return dao.findById(id);
    }

    public List<Animal> findAll() {
        return dao.findAll();
    }

    public List<Animal> findByType(Class<? extends Animal> type) {
        return dao.findByType(type);
    }

    public void release(UUID id) {
        dao.delete(id);
    }

    public Optional<Animal> findHeaviest() {
        return dao.findHeaviest();
    }

    public List<Animal> findOlderThan(int years) {
        return dao.findOlderThan(years);
    }

    public double averageAge() {
        return dao.averageAge();
    }

    public Map<Class<? extends Animal>, Long> countByType() {
        return dao.countByType();
    }
}
