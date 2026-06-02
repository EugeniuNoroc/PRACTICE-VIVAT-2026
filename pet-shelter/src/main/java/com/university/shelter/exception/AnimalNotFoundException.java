package com.university.shelter.exception;

import java.util.UUID;

public class AnimalNotFoundException extends ShelterException{
    public AnimalNotFoundException(UUID id){
        // REVIEW[GOOD] (закрыт долг из ревью 1.2): пробелы вокруг id теперь на месте.
        // Микро-улучшение на будущее: "Животное с id %s не найдено".formatted(id) — читаемее конкатенации.
        super("Животное с айди " + id + " не найдено");
    }
}
