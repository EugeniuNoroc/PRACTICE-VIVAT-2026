package com.university.shelter.dao;

import com.university.shelter.db.ConnectionFactory;
import com.university.shelter.exception.AnimalNotFoundException;
import com.university.shelter.exception.DaoException;
import com.university.shelter.model.Animal;
import com.university.shelter.model.Cat;
import com.university.shelter.model.Dog;
import com.university.shelter.model.HealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class AnimalDaoJdbc implements AnimalDao {

    private static final Logger logger = LoggerFactory.getLogger(AnimalDaoJdbc.class);

    private Animal mapAnimal(ResultSet rs, Connection conn) throws SQLException {
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
                    return new Dog(animalId, name, birthDate, weight, healthStatus,
                            dogRs.getString("breed"), dogRs.getInt("obedience_level"));
                }
            }
        } else if (type.equals("Cat")) {
            try (PreparedStatement catStmt = conn.prepareStatement("SELECT * FROM cats WHERE animal_id = ?")) {
                catStmt.setObject(1, animalId);
                ResultSet catRs = catStmt.executeQuery();
                if (catRs.next()) {
                    return new Cat(animalId, name, birthDate, weight, healthStatus,
                            catRs.getString("breed"), catRs.getBoolean("indoor_only"));
                }
            }
        }
        throw new DaoException("Неизвестный тип животного: " + type, null);
    }

    public void saveWithConnection(Animal animal, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO animals (id, name, birth_date, weight, health_status, type) VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setObject(1, animal.getId());
            stmt.setString(2, animal.getName());
            stmt.setDate(3, Date.valueOf(animal.getBirthDate()));
            stmt.setDouble(4, animal.getWeight());
            stmt.setString(5, animal.getHealthStatus().name());
            stmt.setString(6, animal.getClass().getSimpleName());
            logger.info("Сохраняем животное: {}", animal.getName());
            stmt.executeUpdate();

            if (animal instanceof Dog dog) {
                try (PreparedStatement dogStmt = conn.prepareStatement(
                        "INSERT INTO dogs (animal_id, breed, obedience_level) VALUES (?, ?, ?)")) {
                    dogStmt.setObject(1, dog.getId());
                    dogStmt.setObject(2, dog.getBreed());
                    dogStmt.setObject(3, dog.getObedienceLevel());
                    dogStmt.executeUpdate();
                }
            } else if (animal instanceof Cat cat) {
                try (PreparedStatement catStmt = conn.prepareStatement(
                        "INSERT INTO cats (animal_id, breed, indoor_only) VALUES (?, ?, ?)")) {
                    catStmt.setObject(1, cat.getId());
                    catStmt.setObject(2, cat.getBreed());
                    catStmt.setObject(3, cat.isIndoorOnly());
                    catStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка при сохранении животного", e);
        }
    }

    @Override
    public void save(Animal animal) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            saveWithConnection(animal, conn);
            conn.commit();
        } catch (SQLException e) {
            throw new DaoException("Ошибка подключения", e);
        }
    }


    public void acceptBatch(List<Animal> animals) {
        Connection conn = ConnectionFactory.getConnection();
        try {
            conn.setAutoCommit(false);
            for (Animal animal : animals) {
                saveWithConnection(animal, conn);
            }
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new DaoException("Ошибка rollback", ex);
            }
            throw new DaoException("Ошибка при сохранении batch", e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn("Не удалось закрыть соединение", e);
            }
        }
    }

    @Override
    public Optional<Animal> findById(UUID id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM animals WHERE id = ?")) {

            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapAnimal(rs, conn));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Ошибка при поиске животного", e);
        }
    }

    @Override
    public List<Animal> findAll() {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM animals")) {
            ResultSet rs = stmt.executeQuery();
            List<Animal> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapAnimal(rs, conn));
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException("Ошибка при получении всех животных", e);
        }
    }

    @Override
    public List<Animal> findByType(Class<? extends Animal> type) {
        String typeName = type.getSimpleName();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM animals WHERE type = ?")) {
            stmt.setString(1, typeName);
            ResultSet rs = stmt.executeQuery();
            List<Animal> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapAnimal(rs, conn));
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException("Ошибка при поиске по типу", e);
        }
    }

    @Override
    public void update(Animal animal) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE animals SET name=?, birth_date=?, weight=?, health_status=? WHERE id=?")) {
            stmt.setString(1, animal.getName());
            stmt.setDate(2, Date.valueOf(animal.getBirthDate()));
            stmt.setDouble(3, animal.getWeight());
            stmt.setString(4, animal.getHealthStatus().name());
            stmt.setObject(5, animal.getId());
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new AnimalNotFoundException(animal.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка при обновлении животного", e);
        }
    }

    @Override
    public void delete(UUID id) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement("DELETE FROM animals WHERE id = ?")) {
                stmt.setObject(1, id);
                int deleted = stmt.executeUpdate();
                if (deleted == 0) {
                    throw new AnimalNotFoundException(id);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка при удалении животного", e);
        }
    }

    @Override
    public Optional<Animal> findHeaviest() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Animal> findOlderThan(int years) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public double averageAge() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map<Class<? extends Animal>, Long> countByType() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
