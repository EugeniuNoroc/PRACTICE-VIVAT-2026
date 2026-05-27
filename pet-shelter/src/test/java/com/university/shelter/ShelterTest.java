package com.university.shelter;

import com.university.shelter.dao.InMemoryAnimalDao;
import com.university.shelter.exception.AnimalNotFoundException;
import com.university.shelter.exception.DuplicateAnimalException;
import com.university.shelter.exception.InvalidAnimalDataException;
import com.university.shelter.model.Animal;
import com.university.shelter.model.Cat;
import com.university.shelter.model.Dog;
import com.university.shelter.model.HealthStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShelterTest {
    private ShelterService service;
    private Cat testCat;
    private Dog testDog;

    @BeforeEach
    void setUp() {
        service = new ShelterService(new InMemoryAnimalDao());
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

    @DisplayName("Добавить животное когда данные переданы верно")
    @Test
    void accept_shouldAddAnimal_whenAnimalIsValid() {
        //ARRANGE
        service.accept(testCat);

        //ACT
        Optional<Animal> result = service.findById(testCat.getId());

        //ASSERT
        assertThat(result).isPresent();
    }

    @DisplayName("Ничего не вернуть если животное не найдено")
    @Test
    void findById_shouldReturnEmpty_ifAnimalNotPresent() {
        //ACT
        Optional<Animal> result = service.findById(UUID.randomUUID());

        //ASSERT
        assertThat(result).isEmpty();
    }

    @DisplayName("Удалить животину если она есть")
    @Test
    void remove_shouldRemoveAnimal_ifAnimalExists() {
        //ARRANGE
        service.accept(testCat);
        service.release(testCat.getId());

        //ACT
        Optional<Animal> result = service.findById(testCat.getId());

        //ASSERT
        assertThat(result).isEmpty();
    }

    @DisplayName("Получить AnimalNotFoundException если животину не нашли при удалении")
    @Test
    void remove_shouldThrowException_ifAnimalNotPresent() {
        //ASSERT+ACT
        assertThatThrownBy(() -> service.release(testCat.getId())).isInstanceOf(AnimalNotFoundException.class);
    }

    @DisplayName("Получить животных по принадлежности к классу")
    @Test
    void findByType_shouldReturnCat_ifAnimalIsCat() {
        //ARRANGE
        service.accept(testCat);
        service.accept(testDog);

        //ACT
        List<Animal> result = service.findByType(Cat.class);

        //ASSERT
        assertThat(result).hasSize(1).allMatch(a -> a instanceof Cat);
    }

    //Параметизированный тест на проверку пустого имени, null и пробелов
    @DisplayName("Получить InvalidAnimalDataException если введенный ник животного плохой")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void animal_shouldThrowException_whenNameIsInvalid(String invalidName) {
        //ASSERT
        assertThatThrownBy(() -> new Cat(
                UUID.randomUUID(),
                invalidName,
                LocalDate.of(2020, 1, 1),
                4.5,
                HealthStatus.RECOVERING,
                "Persian",
                true))
                .isInstanceOf(InvalidAnimalDataException.class);
    }

    @DisplayName("Получить InvalidAnimalDataException если вес отрицательный")
    @ParameterizedTest
    @CsvSource({
            "-1, Вес не может быть меньше 0.",
            "-25, Вес не может быть меньше 0.",
            "-0.2, Вес не может быть меньше 0."
    })
    void validate_shouldThrowException_ifWeightIsNotValid(double invalidWeight, String expectedMessage){
        assertThatThrownBy(() -> new Cat(
                UUID.randomUUID(),
                "КотТипа",
                LocalDate.of(2020, 1, 1),
                invalidWeight,
                HealthStatus.RECOVERING,
                "Persian",
                true))
                .isInstanceOf(InvalidAnimalDataException.class).hasMessage(expectedMessage);
    }

    @DisplayName("Получить толстейшую животину если животины есть")
    @Test
     void getHeaviest_shouldReturnHeaviestAnimal_ifAnimalExists() {
        //ARRANGE
        service.accept(testDog);
        service.accept(testCat);

        //ACT
        Optional<Animal> result = service.findHeaviest();

        //ASSERT
        assertThat(result).isPresent().contains(testDog);
    }

    @DisplayName("Получить старейшую животину если животины есть")
    @Test
    void findOlderThan_shouldReturnOldest_ifAnimalExists() {
        //ARRANGE
        service.accept(testDog);
        service.accept(testCat);

        //ACT
        List<Animal> result = service.findOlderThan(7);

        //ASSERT
        assertThat(result).isNotEmpty().contains(testDog).doesNotContain(testCat);
    }

    @DisplayName("Вернуть средний возраст животин если они есть")
    @Test
    void averageAge_shouldReturnAverageAgeOfAnimals_ifAnimalsExists(){
        //ARRANGE
        service.accept(testDog);
        service.accept(testCat);

        //ACT
        double result = service.averageAge();

        //ASSERT
        assertThat(result).isGreaterThan(6).isLessThan(10);
    }

    @DisplayName("Посчитать животин по классам если они есть")
    @Test
    void countByType_shouldReturnAmountOfTypeAnimals_ifAnimalsExists(){
        //ARRANGE
        service.accept(testDog);
        service.accept(testCat);

        //ACT
        Map<Class<? extends Animal>, Long> result = service.countByType();

        //ASSERT
        assertThat(result).isNotEmpty().containsEntry(Cat.class, 1L).containsEntry(Dog.class, 1L); //1L - что-то типа обертки, вроде интовый 1, но на деле объект
    }

    @DisplayName("Лутануть DuplicateAnimalException если принятое животное уже есть")
    @Test
    void accept_shouldReturnException_ifAnimalDuplicate(){
        //ARRANGE
        service.accept(testDog);

        //ACT+ASSERT
        assertThatThrownBy(() -> service.accept(testDog)).isInstanceOf(DuplicateAnimalException.class);
    }
}