package com.university.library.exception;

public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(Long id) {
        super("Книга с id " + id + " не найдена");
    }
}