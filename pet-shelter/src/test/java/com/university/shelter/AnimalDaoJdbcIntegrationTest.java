package com.university.shelter;

import com.university.shelter.dao.AnimalDaoJdbc;
import com.university.shelter.db.ConnectionFactory;
import com.university.shelter.exception.DaoException;
import com.university.shelter.model.Animal;
import com.university.shelter.model.Cat;
import com.university.shelter.model.Dog;
import com.university.shelter.model.HealthStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
public class AnimalDaoJdbcIntegrationTest {
    private AnimalDaoJdbc dao;
    private Dog testDog;
    private Cat testCat;

    @BeforeEach
    void setUp() {
        dao = new AnimalDaoJdbc();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM animals;")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Ошибка при удалении", e);
        }
        testDog = new Dog(
                UUID.randomUUID(),
                "Шарик",
                LocalDate.of(2018, 5, 10),
                12.0,
                HealthStatus.HEALTHY,
                "Beagle",
                8
        );
        testCat = new Cat(
                UUID.randomUUID(),
                "Мурзик",
                LocalDate.of(2015, 2, 2),
                13.0,
                HealthStatus.SICK,
                "Beagle",
                true
        );
    }

    @Test
    void save_shouldSaveAnimal_andFindById_shouldReturnIt() {
        // ACT
        dao.save(testDog);

        // ASSERT
        Optional<Animal> result = dao.findById(testDog.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Шарик");
    }

    @Test
    void delete_shouldDeleteAnimal_andFindById_shouldReturnEmptyOptional() {
        // ACT
        dao.save(testDog);
        dao.delete(testDog.getId());

        // ASSERT
        Optional<Animal> result = dao.findById(testDog.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllAnimals_ifTheyExists() {
        // ACT
        dao.save(testCat);
        dao.save(testDog);

        // ASSERT
        Optional<Animal> resultDog = dao.findById(testDog.getId());
        Optional<Animal> resultCat = dao.findById(testCat.getId());
        assertThat(resultDog).isPresent();
        assertThat(resultCat).isPresent();
        assertThat(resultDog.get().getName()).isEqualTo("Шарик");
        assertThat(resultCat.get().getName()).isEqualTo("Мурзик");
    }

    @AfterEach
    void tearDown() {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM animals;")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Ошибка при удалении", e);
        }
    }
}
