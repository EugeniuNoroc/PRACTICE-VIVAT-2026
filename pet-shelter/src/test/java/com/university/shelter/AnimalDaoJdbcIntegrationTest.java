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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
public class AnimalDaoJdbcIntegrationTest {
    private AnimalDaoJdbc dao;
    private Dog testDog;
    private Cat testCat;
    private Cat testCat2;
    private Animal duplicate;

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
                "MaybeBeagle",
                true
        );
        testCat2 = new Cat(
                UUID.randomUUID(),
                "Мурзк",
                LocalDate.of(2013, 3, 4),
                12.0,
                HealthStatus.HEALTHY,
                "Persian",
                true
        );
        duplicate = new Dog(
                testDog.getId(), // тот же UUID!
                "Дубликат",
                LocalDate.now(),
                5.0,
                HealthStatus.HEALTHY,
                "Labrador",
                3
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

    @Test
    void acceptBatch_shouldReturnAllAnimals_ifSavedWithSuccess(){
        // ARRANGE
        List<Animal> animals = List.of(testCat, testCat2, testDog);

        // ACT
        dao.acceptBatch(animals);
        List<Animal> saved = dao.findAll();

        // ASSERT
        assertThat(saved).hasSize(3);
    }

    @Test
    void acceptBatch_shouldRollback_ifInvalidObject(){
        // ARRANGE
        dao.save(testDog);
        List<Animal> animals = List.of(testCat, duplicate, testCat2, testDog);

        // ASSERT
        assertThatThrownBy(() -> dao.acceptBatch(animals)).isInstanceOf(DaoException.class);
        assertThat(dao.findAll()).hasSize(1);
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
