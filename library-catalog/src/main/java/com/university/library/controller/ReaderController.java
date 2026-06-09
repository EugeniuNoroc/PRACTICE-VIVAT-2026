package com.university.library.controller;

import com.university.library.controller.dto.ReaderCreateRequest;
import com.university.library.controller.dto.ReaderUpdateRequest;
import com.university.library.model.Reader;
import com.university.library.service.ReaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;
    private final ReaderMapper readerMapper;

    public ReaderController(ReaderService readerService, ReaderMapper readerMapper) {
        this.readerService = readerService;
        this.readerMapper = readerMapper;
    }

    @GetMapping
    public ResponseEntity<List<Reader>> findAll() {
        return ResponseEntity.ok(readerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reader> findById(@PathVariable UUID id) {
        return readerService.findById(id)
                .map(reader -> ResponseEntity.ok(reader))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ReaderCreateRequest request) {
        readerService.save(readerMapper.toEntity(request));
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody ReaderUpdateRequest request) {
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