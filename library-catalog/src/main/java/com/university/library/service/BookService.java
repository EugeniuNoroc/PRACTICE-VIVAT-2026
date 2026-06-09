package com.university.library.service;

import com.university.library.controller.dto.BookUpdateRequest;
import com.university.library.controller.dto.PagedResponse;
import com.university.library.exception.BookNotFoundException;
import com.university.library.model.Book;
import com.university.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }
    
    public PagedResponse<Book> findAll(int page, int size) {
        int offset = page * size;
        List<Book> books = bookRepository.findAll(offset, size);
        long total = bookRepository.count();
        return new PagedResponse<>(books, page, size, total);
    }

    public void save(Book book) {
        bookRepository.save(book);
    }

    public Optional<Book> findById(UUID id) { return bookRepository.findById(id); }

    public void update(UUID id, BookUpdateRequest request) {
        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        Book update = new Book(
                existingBook.getId(),
                request.title(),
                existingBook.getIsbn(),
                existingBook.getYear(),
                existingBook.getAuthorId(),
                request.copiesTotal(),
                existingBook.getCopiesAvailable()
        );
        bookRepository.update(update);
    }

    public void delete(UUID id) {
        bookRepository.delete(id);
    }
}
