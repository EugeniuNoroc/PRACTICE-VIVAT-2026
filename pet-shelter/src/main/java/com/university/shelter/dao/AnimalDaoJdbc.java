package com.university.shelter.dao;

import com.university.shelter.db.ConnectionFactory;
import com.university.shelter.exception.DaoException;
import com.university.shelter.model.Animal;
import com.university.shelter.model.Cat;
import com.university.shelter.model.Dog;
import com.university.shelter.model.HealthStatus;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AnimalDaoJdbc implements AnimalDao {
    @Override
    public void save(Animal animal) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO animals (id, name, birth_date, weight, health_status, type) VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setObject(1, animal.getId());
            stmt.setString(2, animal.getName());
            stmt.setDate(3, Date.valueOf(animal.getBirthDate()));
            stmt.setDouble(4, animal.getWeight());
            stmt.setString(5, animal.getHealthStatus().name());
            stmt.setString(6, animal.getClass().getSimpleName());
            stmt.executeUpdate();
            if (animal instanceof Dog dog) {
                try(PreparedStatement dogStmt = conn.prepareStatement("INSERT INTO dogs (animal_id, breed, obedience_level) VALUES (?, ?, ?)")) {
                    dogStmt.setObject(1, dog.getId());
                    dogStmt.setObject(2, dog.getBreed());
                    dogStmt.setObject(3, dog.getObedienceLevel());
                    dogStmt.executeUpdate();
                }
            } else if (animal instanceof Cat cat) {
                try(PreparedStatement catStmt = conn.prepareStatement("INSERT INTO cats (animal_id, breed, indoor_only) VALUES (?, ?, ?)")) {
                    catStmt.setObject(1, cat.getId());
                    catStmt.setObject(2, cat.getBreed());
                    catStmt.setObject(3, cat.isIndoorOnly());
                    catStmt.executeUpdate();
                }
            }
        } catch (SQLException | IOException e) {
            throw new DaoException("Ошибка при сохранении животного", e);
        }
    }

    @Override
    public Optional<Animal> findById(UUID id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM animals WHERE id = ?")) {

            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UUID animalId = UUID.fromString(rs.getString("id"));
                String name = rs.getString("name");
                LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
                double weight = rs.getDouble("weight");
                HealthStatus healthStatus = HealthStatus.valueOf(rs.getString("health_status"));
                String type = rs.getString("type");

                if (type.equals("Dog")) {
                    try (PreparedStatement dogStmt = conn.prepareStatement("SELECT * FROM dogs WHERE animal_id = ?")) {
                        dogStmt.setObject(1, animalId);
                        ResultSet dogRs = dogStmt.executeQuery();
                        if (dogRs.next()) {
                            String breed = dogRs.getString("breed");
                            int obedienceLevel = dogRs.getInt("obedience_level");
                            return Optional.of(new Dog(animalId, name, birthDate, weight, healthStatus, breed, obedienceLevel));
                        }
                    }
                } else if (type.equals("Cat")) {
                    try (PreparedStatement catStmt = conn.prepareStatement("SELECT * FROM cats WHERE animal_id = ?")) {
                        catStmt.setObject(1, animalId);
                        ResultSet catRs = catStmt.executeQuery();
                        if (catRs.next()) {
                            String breed = catRs.getString("breed");
                            boolean indoorOnly = catRs.getBoolean("indoor_only");
                            return Optional.of(new Cat(animalId, name, birthDate, weight, healthStatus, breed, indoorOnly));
                        }
                    }
                }
            }
            return Optional.empty();

        } catch (SQLException | IOException e) {
            throw new DaoException("Ошибка при поиске животного", e);
        }
    }

    @Override
    public List<Animal> findAll() {
        return List.of();
    }

    @Override
    public List<Animal> findByType(Class<? extends Animal> type) {
        return List.of();
    }

    @Override
    public void update(Animal animal) {

    }

    @Override
    public void delete(UUID id) {

    }
}
