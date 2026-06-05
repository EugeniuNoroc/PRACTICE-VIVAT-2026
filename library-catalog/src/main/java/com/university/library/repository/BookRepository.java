package com.university.library.repository;

import com.university.library.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {
    private final List<Book> books = new ArrayList<>(); // final - еременная не может быть назначена на другой список

    public List<Book> findAll() {
        return books;
    }

    public Optional<Book> findById(Long id) {
        // TODO: реализовать
        return Optional.empty();
    }

    public void save(Book book) {
        books.add(book);
    }

    public void update(Book book) {
        // TODO: реализовать
    }

    public void delete(Long id) {
        // TODO: реализовать
    }
}
