package com.university.shelter.dao;

import com.university.shelter.model.Animal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnimalDao {
    void save(Animal animal) throws SQLException, IOException;
    Optional<Animal> findById(UUID id);
    List<Animal> findAll();
    List<Animal> findByType(Class<? extends Animal> type);
    void update(Animal animal);
    void delete(UUID id);
}
