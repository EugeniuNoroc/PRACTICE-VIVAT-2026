package com.university.shelter.model;

import java.time.LocalDate;
import java.util.UUID;

public class Dog extends Animal implements Feedable {

    private final String breed;
    private final int obedienceLevel;

    public Dog(UUID id, String name, LocalDate birthDate, double weight, HealthStatus healthStatus, String breed, int obedienceLevel){
        super(id, name, birthDate, weight, healthStatus);

        if(breed == null){
            throw new IllegalArgumentException("Поле должно быть заполнено");
        }

        if(obedienceLevel < 0){
            throw new IllegalArgumentException("Поле не может быть отрицательным");
        }

        this.breed = breed;
        this.obedienceLevel = obedienceLevel;
    }

    @Override
    public String makeSound() {
        return "Woof";
    }

    public int getObedienceLevel(){
        return obedienceLevel;
    }

    public String getBreed(){
        return breed;
    }

    @Override
    public void feed(FoodPortion food){
        System.out.println("Собака жадно поедает " + food.foodPortion() + "в количестве" + food.grams());
    }
}
