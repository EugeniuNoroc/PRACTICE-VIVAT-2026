package com.university.library.exception;

public class ReaderNotFoundException extends LibraryException {
    public ReaderNotFoundException(Long id) {
        super("Читатель с id " + id + " не найден");
    }
}