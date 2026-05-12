package com.university.shelter.model;

import java.time.LocalDate;
import java.util.UUID;

public class Persian extends Cat{

    private final int playfulness;

    public Persian(UUID id, String name, LocalDate birthDate, double weight, HealthStatus healthStatus, String breed, boolean indoorOnly, int playfulness){
        super(id, name, birthDate, weight, healthStatus, breed, indoorOnly);

        this.playfulness = playfulness;
    }

    public int getPlayfulness(){
        return this.playfulness;
    }
}
