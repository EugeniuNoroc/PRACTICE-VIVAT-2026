package com.university.library.exception;

import java.util.UUID;

public class BookAlreadyLoanedException extends LibraryException {
    public BookAlreadyLoanedException(UUID bookId) {
        super("Книга с id " + bookId + " уже выдана");
    }
}