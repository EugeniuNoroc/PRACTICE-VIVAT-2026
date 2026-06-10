package com.university.library.controller;

import com.university.library.controller.dto.AuthorCreateRequest;
import com.university.library.controller.dto.AuthorResponse;
import com.university.library.controller.dto.AuthorUpdateRequest;
import com.university.library.model.Author;
import com.university.library.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    public AuthorController(AuthorService authorService, AuthorMapper authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    @Operation(summary = "Найти всех авторов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Авторы найдены, данные возвращены")
    })
    @GetMapping
    public ResponseEntity<List<AuthorResponse>> findAll() {
        return ResponseEntity.ok(
                authorService.findAll().stream()
                        .map(authorMapper::toResponse)
                        .toList()
        );
    }

    @Operation(summary = "Найти автора по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Автор найден, данные возвращены"),
            @ApiResponse(responseCode = "404", description = "Автор не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> findById(@PathVariable UUID id) {
        return authorService.findById(id)
                .map(author ->  ResponseEntity.ok(authorMapper.toResponse(author)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать автора")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Автор создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
    })
    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody AuthorCreateRequest request) {
        Author newAuthor = authorMapper.toEntity(request);
        authorService.save(newAuthor);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Обновить данные автора")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Автор обновлен"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "404", description = "Автор не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody AuthorUpdateRequest request) {
        if(authorService.findById(id).isPresent()) {
            authorService.update(id, request);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить автора")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Автор удален"),
            @ApiResponse(responseCode = "404", description = "Автор не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if(authorService.findById(id).isPresent()) {
            authorService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}