package com.university.shelter.model;

import com.university.shelter.exception.InvalidAnimalDataException;

import java.time.LocalDate;
import java.util.UUID;

public class Cat extends Animal implements Feedable {

    private final String breed;
    private final boolean indoorOnly;

    public Cat(UUID id, String name, LocalDate birthDate, double weight, HealthStatus healthStatus, String breed, boolean indoorOnly){
        super(id, name, birthDate, weight, healthStatus);

        if(breed == null){
            throw new InvalidAnimalDataException("Поле должно быть заполнено");
        }

        this.breed = breed;
        this.indoorOnly = indoorOnly;
    }

    @Override
    public String makeSound() {
        return "Meow";
    }

    public boolean isIndoorOnly() { return this.indoorOnly; }

    public String getBreed() { return this.breed; }

    @Override
    public void feed(FoodPortion food){
        System.out.println("Кошка капризно пробует " + food.foodPortion() + "в количестве" + food.grams());
    }
}
