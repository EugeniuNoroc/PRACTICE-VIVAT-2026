package com.university.library.controller;

import com.university.library.controller.dto.BookCreateRequest;
import com.university.library.controller.dto.BookResponse;
import com.university.library.controller.dto.BookUpdateRequest;
import com.university.library.model.Author;
import com.university.library.model.Book;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BookMapper {

    public BookResponse toResponse(Book book, Author author) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getYear(),
                author.getFullName(),
                book.getCopiesAvailable()
        );
    }

    public Book toEntity(BookCreateRequest request) {
        return new Book(
                UUID.randomUUID(),
                request.title(),
                request.isbn(),
                request.year(),
                request.authorId(),
                request.copiesTotal(), // copiesTotal
                request.copiesTotal()  // copiesAvailable - все копии свободны при создании
        );
    }

    public Book applyUpdate(Book book, BookUpdateRequest request) {
        return new Book(
                book.getId(),
                request.title(),
                book.getIsbn(),
                book.getYear(),
                book.getAuthorId(),
                request.copiesTotal(),
                book.getCopiesAvailable()
        );
    }
}
