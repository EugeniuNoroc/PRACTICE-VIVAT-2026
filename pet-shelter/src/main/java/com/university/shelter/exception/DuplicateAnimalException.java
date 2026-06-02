package com.university.shelter.exception;

import java.util.UUID;

public class DuplicateAnimalException extends ShelterException{
    public DuplicateAnimalException(UUID id){
        // REVIEW[DEBT] (с ревью 1.2): пропал пробел перед "уже" -> "...айди XXXуже есть".
        // (В AnimalNotFoundException пробелы ты уже починил — здесь осталось.) Лучше:
        //   super("Животное с id %s уже есть в системе".formatted(id));
        super("Животное с айди " + id + "уже есть в системе");
    }
}
