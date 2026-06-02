package com.university.shelter.exception;

import java.util.UUID;

public class DuplicateAnimalException extends ShelterException{
    public DuplicateAnimalException(UUID id){
        super("Животное с айди " + id + " уже есть в системе");
    }
}
