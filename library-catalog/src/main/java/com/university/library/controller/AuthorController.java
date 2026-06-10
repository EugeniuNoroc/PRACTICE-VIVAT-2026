package com.university.library.controller;

import com.university.library.controller.dto.AuthorCreateRequest;
import com.university.library.controller.dto.AuthorResponse;
import com.university.library.controller.dto.AuthorUpdateRequest;
import com.university.library.model.Author;
import com.university.library.service.AuthorService;
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

    @GetMapping
    public ResponseEntity<List<AuthorResponse>> findAll() {
        return ResponseEntity.ok(
                authorService.findAll().stream()
                        .map(authorMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> findById(@PathVariable UUID id) {
        return authorService.findById(id)
                .map(author ->  ResponseEntity.ok(authorMapper.toResponse(author)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody AuthorCreateRequest request) {
        Author newAuthor = authorMapper.toEntity(request);
        authorService.save(newAuthor);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody AuthorUpdateRequest request) {
        if(authorService.findById(id).isPresent()) {
            authorService.update(id, request);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if(authorService.findById(id).isPresent()) {
            authorService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
