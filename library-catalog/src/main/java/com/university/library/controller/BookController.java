package com.university.library.controller;

import com.university.library.controller.dto.BookCreateRequest;
import com.university.library.controller.dto.BookResponse;
import com.university.library.controller.dto.BookUpdateRequest;
import com.university.library.controller.dto.PagedResponse;
import com.university.library.model.Book;
import com.university.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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

    @Operation(summary = "Найти все книги")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книги найдены, данные возвращены")
    })
    @GetMapping
    public ResponseEntity<PagedResponse<BookResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.findAll(page, size));
    }

    @Operation(summary = "Найти книгу по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книга найдена, данные возвращены"),
            @ApiResponse(responseCode = "404", description = "Книга не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable UUID id) {
        return bookService.findByIdWithAuthor(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать книгу")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Книга создана"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "409", description = "ISBN уже существует")
    })
    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody BookCreateRequest request) {
        Book newBook = bookMapper.toEntity(request);
        bookService.save(newBook);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Обновить книгу")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Книга обновлена"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "404", description = "Книга не найдена")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody BookUpdateRequest request) {
        if (bookService.findById(id).isPresent()) {
            bookService.update(id, request);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить книгу")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Книга удалена"),
            @ApiResponse(responseCode = "404", description = "Книга не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (bookService.findById(id).isPresent()) {
            bookService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
