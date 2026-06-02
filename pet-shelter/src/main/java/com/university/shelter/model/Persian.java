package com.university.shelter.model;

import com.university.shelter.exception.InvalidAnimalDataException;

import java.time.LocalDate;
import java.util.UUID;

public class Persian extends Cat{

    private final int playfulness;

    public Persian(UUID id, String name, LocalDate birthDate, double weight, HealthStatus healthStatus, String breed, boolean indoorOnly, int playfulness){
        super(id, name, birthDate, weight, healthStatus, breed, indoorOnly);

        if(playfulness < 0) {
            throw new InvalidAnimalDataException("Playfulness не может быть отрицательным.");
        }
        this.playfulness = playfulness;

    }

    public int getPlayfulness(){
        return this.playfulness;
    }
}
