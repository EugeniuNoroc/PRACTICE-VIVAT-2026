package com.university.shelter;

import com.university.shelter.model.Animal;
import com.university.shelter.model.Cat;
import com.university.shelter.model.Dog;
import com.university.shelter.model.HealthStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShelterTest {
    private Shelter shelter;
    private Cat testCat;
    private Dog testDog;

    @BeforeEach
    void setUp() {
        shelter = new Shelter();
        testCat = new Cat(
                UUID.randomUUID(),
                "ИмяКота",
                LocalDate.of(2020, 1, 1),
                4.5,
                HealthStatus.RECOVERING,
                "Persian",
                true
        );
        testDog = new Dog(
                UUID.randomUUID(),
                "Шарик",
                LocalDate.of(2018, 5, 10),
                12.0,
                HealthStatus.HEALTHY,
                "Beagle",
                8
        );
    }

    @Test
    void accept_shouldAddAnimal_whenAnimalIsValid(){
        //ACT
        shelter.accept(testCat);

        //ASSERT
        Optional<Animal> result = shelter.findById(testCat.getId());
        assertThat(result).isPresent();
    }

    @Test
    void findById_shouldReturnEmpty_ifAnimalNotPresent(){
        //ACT
        Optional<Animal> result = shelter.findById(UUID.randomUUID());

        //ASSERT
        assertThat(result).isEmpty();
    }

    @Test
    void remove_shouldRemoveAnimal_ifAnimalExists(){
        //ACT
        shelter.accept(testCat);
        shelter.release(testCat.getId());

        //ASSERT
        Optional<Animal> result = shelter.findById(testCat.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void remove_shouldDoNothing_ifAnimalNotPresent(){
        //ACT
        shelter.release(testCat.getId());

        //ASSERT
        Optional<Animal> result = shelter.findById(testCat.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void findByType_shouldReturnCat_ifAnimalIsCat(){
        //ACT
        shelter.accept(testCat);
        shelter.accept(testDog);

        //ASERT
        List<Animal> result = shelter.findByType(Cat.class);
        assertThat(result).hasSize(1).allMatch(a -> a instanceof Cat);
    }

    //Параметизированный тест на проверку пустого имени, null и пробелов
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource (strings = {"", " ", "  "})
    void animal_shouldThrowException_whenNameIsInvalid(String invalidName){
        //ASERT
        assertThatThrownBy(() -> new Cat(
                UUID.randomUUID(),
                invalidName,
                LocalDate.of(2020, 1, 1),
                4.5,
                HealthStatus.RECOVERING,
                "Persian",
                true))
        .isInstanceOf(IllegalArgumentException.class);
    }
}
