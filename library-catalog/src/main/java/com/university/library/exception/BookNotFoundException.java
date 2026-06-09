package com.university.library.exception;

import java.util.UUID;

public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(UUID id) {
        super("Книга с id " + id + " не найдена");
    }
}