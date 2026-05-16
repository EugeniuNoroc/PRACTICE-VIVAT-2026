package com.university.shelter.model;

import com.university.shelter.exception.InvalidAnimalDataException;

import java.time.LocalDate;
import java.util.UUID;

public class Beagle extends Dog{

    private final int devotion;

    public Beagle(UUID id, String name, LocalDate birthDate, double weight, HealthStatus healthStatus, String breed, int obedianceLevel, int devotion){
        super(id, name, birthDate, weight, healthStatus, breed, obedianceLevel);

        if(devotion < 0){
            throw new InvalidAnimalDataException("Поле не может быть отрицательным.");
        }

        this.devotion = devotion;
    }

    public int getDevotion(){
        return devotion;
    }
}
