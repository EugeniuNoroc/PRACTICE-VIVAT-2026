package com.university.library.controller;

import com.university.library.controller.dto.ReaderCreateRequest;
import com.university.library.controller.dto.ReaderResponse;
import com.university.library.controller.dto.ReaderUpdateRequest;
import com.university.library.service.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Найти всех читателей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Читатели найдены, данные возвращены")
    })
    @GetMapping
    public ResponseEntity<List<ReaderResponse>> findAll() {
        return ResponseEntity.ok(
                readerService.findAll().stream()
                        .map(readerMapper::toResponse)
                        .toList()
        );
    }

    @Operation(summary = "Найти читателя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Читатель найден, данные возвращены"),
            @ApiResponse(responseCode = "404", description = "Автор не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReaderResponse> findById(@PathVariable UUID id) {
        return readerService.findById(id)
                .map(reader -> ResponseEntity.ok(readerMapper.toResponse(reader)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать читателя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Читатель создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "409", description = "Читатель с таким email уже существует")
    })
    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody ReaderCreateRequest request) {
        readerService.save(readerMapper.toEntity(request));
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Обновить данные читателя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Читатель обновлен"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "404", description = "Читатель не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody ReaderUpdateRequest request) {
        if (readerService.findById(id).isPresent()) {
            readerService.update(id, request);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить читателя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Читатель удален"),
            @ApiResponse(responseCode = "404", description = "Читатель не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (readerService.findById(id).isPresent()) {
            readerService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}