package com.university.library.controller;

import com.university.library.controller.dto.ReaderCreateRequest;
import com.university.library.controller.dto.ReaderResponse;
import com.university.library.controller.dto.ReaderUpdateRequest;
import com.university.library.service.ReaderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;
    private final ReaderMapper readerMapper;

    public ReaderController(ReaderService readerService, ReaderMapper readerMapper, AuthorMapper authorMapper) {
        this.readerService = readerService;
        this.readerMapper = readerMapper;
    }

    @GetMapping
    public ResponseEntity<List<ReaderResponse>> findAll() {
        return ResponseEntity.ok(
                readerService.findAll().stream()
                        .map(readerMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReaderResponse> findById(@PathVariable UUID id) {
        return readerService.findById(id)
                .map(reader -> ResponseEntity.ok(readerMapper.toResponse(reader)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody ReaderCreateRequest request) {
        readerService.save(readerMapper.toEntity(request));
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody ReaderUpdateRequest request) {
        if (readerService.findById(id).isPresent()) {
            readerService.update(id, request);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (readerService.findById(id).isPresent()) {
            readerService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}