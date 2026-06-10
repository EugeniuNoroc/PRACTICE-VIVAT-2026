package com.university.library.exception;

public class EntityNotFoundException extends LibraryException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}