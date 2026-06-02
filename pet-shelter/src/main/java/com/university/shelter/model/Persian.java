package com.university.shelter.model;

import java.time.LocalDate;
import java.util.UUID;

public class Persian extends Cat{

    private final int playfulness;

    public Persian(UUID id, String name, LocalDate birthDate, double weight, HealthStatus healthStatus, String breed, boolean indoorOnly, int playfulness){
        super(id, name, birthDate, weight, healthStatus, breed, indoorOnly);

        // REVIEW[DEBT] (с ревью 1.1, всё ещё не сделано): playfulness не валидируется,
        // хотя у Beagle.devotion проверка на отрицательное есть. Несогласованность:
        // отрицательные числовые характеристики либо запрещены ВЕЗДЕ, либо нигде.
        // Добавь: if (playfulness < 0) throw new InvalidAnimalDataException("playfulness < 0");
        this.playfulness = playfulness;
    }

    public int getPlayfulness(){
        return this.playfulness;
    }
}
