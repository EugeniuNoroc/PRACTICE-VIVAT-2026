package com.university.shelter.exception;

import java.util.UUID;

public class AnimalNotFoundException extends ShelterException{
    public AnimalNotFoundException(UUID id){
        super("Животное с айди" + id + "не найдено");
    }
}
