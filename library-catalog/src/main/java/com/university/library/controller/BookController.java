package com.university.library.controller;

import com.university.library.controller.dto.BookCreateRequest;
import com.university.library.controller.dto.BookUpdateRequest;
import com.university.library.controller.dto.PagedResponse;
import com.university.library.model.Book;
import com.university.library.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<Book>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findById(@PathVariable UUID id) {
        return bookService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody BookCreateRequest request) {
        Book newBook = bookMapper.toEntity(request);
        bookService.save(newBook);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody BookUpdateRequest request) {
        if (bookService.findById(id).isPresent()) {
            bookService.update(id, request);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (bookService.findById(id).isPresent()) {
            bookService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
