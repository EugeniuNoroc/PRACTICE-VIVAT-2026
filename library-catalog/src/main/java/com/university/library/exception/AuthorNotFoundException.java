package com.university.library.exception;

import java.util.UUID;

public class AuthorNotFoundException extends EntityNotFoundException {
    public AuthorNotFoundException(UUID id) {
        super("Автор с id " + id + " не найден");
    }
}