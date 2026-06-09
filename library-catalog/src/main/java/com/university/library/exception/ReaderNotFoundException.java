package com.university.library.exception;

import java.util.UUID;

public class ReaderNotFoundException extends LibraryException {
    public ReaderNotFoundException(UUID id) {
        super("Читатель с id " + id + " не найден");
    }
}