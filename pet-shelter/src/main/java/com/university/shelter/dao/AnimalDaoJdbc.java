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

    /**
     * REVIEW[BUG] (W1, день 3): метод вызывается в цикле из findAll()/findByType() и на
     * КАЖДОЕ животное делает отдельный SELECT в dogs/cats. Для N животных это 1 + N
     * запросов к БД — классическая проблема N+1 (детально разберём в W3 на Hibernate,
     * но увидеть её "вживую" в своём коде — бесценно).
     *   Прикинь: для 100 животных это 101 запрос. Для 10 000 — 10 001.
     *   Решение: один JOIN — animals LEFT JOIN dogs LEFT JOIN cats, различать по полю type.
     *
     * REVIEW[BUG]: внутренние ResultSet (dogRs/catRs) НЕ обёрнуты в try-with-resources —
     * закрываются только неявно вместе со Statement. Оберни явно (см. ниже).
     *
     * REVIEW[THINK]: метод держит ОДНО соединение и открывает на нём второй Statement,
     * пока ещё не дочитан внешний ResultSet (в findAll). Почему это хрупко, когда
     * появится connection pool или транзакция? (подсказка: курсор + один Connection)
     */
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

    /**
     * REVIEW[BUG] — подвеска на день 5 (тикет 1.5, транзакции):
     * Два INSERT (в animals, затем в dogs/cats) выполняются с autoCommit=true,
     * т.е. КАЖДЫЙ коммитится сам по себе. Если первый прошёл, а второй упал —
     * в animals останется "сирота" без строки в dogs/cats. Битая консистентность.
     * Нужна транзакция: conn.setAutoCommit(false); ... conn.commit(); catch -> conn.rollback();
     *
     * REVIEW[DEBT]: дубликат id сейчас прилетит как SQLException (нарушение PK) и
     * завернётся в общий DaoException. А InMemoryAnimalDao в этом случае бросает
     * DuplicateAnimalException — поведение двух реализаций РАЗНОЕ (нарушение LSP).
     * Лови SQLState "23505" (unique_violation) и бросай DuplicateAnimalException:
     *   catch (SQLException e) {
     *       if ("23505".equals(e.getSQLState())) throw new DuplicateAnimalException(animal.getId());
     *       throw new DaoException("Ошибка при сохранении животного", e);
     *   }
     */
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
            logger.info("Сохраняем животное: {}", animal.getName());
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
        } catch (SQLException e) {
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
                // REVIEW[BUG] (W1, день 3): executeUpdate() возвращает число удалённых строк,
                // но мы его игнорируем -> удаление несуществующего id проходит МОЛЧА.
                // А InMemoryAnimalDao.delete в этом случае кидает AnimalNotFoundException =>
                // две реализации ведут себя по-разному (LSP). Сделай как в update():
                //   int affected = stmt.executeUpdate();
                //   if (affected == 0) throw new AnimalNotFoundException(id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка при удалении животного", e);
        }
    }

    // ========================================================================
    // REVIEW[BUG] (W1, день 3): четыре метода интерфейса AnimalDao НЕ реализованы.
    // Это нарушение LSP: код, работающий через AnimalDao, упадёт на JDBC-реализации,
    // хотя на InMemory работал. Раньше findOlderThan ВРАЛ молча (возвращал пустой
    // список) — сейчас честно бросает UnsupportedOperationException, это уже лучше.
    // Но решить надо по-настоящему (см. JavaDoc в AnimalDao):
    //   а) либо реализовать в SQL (ORDER BY weight DESC LIMIT 1; AVG(...); GROUP BY type);
    //   б) либо вынести аналитику из интерфейса DAO в сервис.
    // ========================================================================
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
