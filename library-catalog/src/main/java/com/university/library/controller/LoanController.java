package com.university.library.controller;

import com.university.library.exception.NoCopiesAvailableException;
import com.university.library.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Operation(summary = "Выдать книгу читателю")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Книга успешно выдана"),
            @ApiResponse(responseCode = "409", description = "Нет доступных экземпляров книги")
    })
    @PostMapping
    public ResponseEntity<Void> issueLoan(@RequestBody UUID bookId, @RequestBody UUID readerId) {
        try {
            loanService.issueLoan(bookId, readerId);
            return ResponseEntity.status(201).build();
        } catch (NoCopiesAvailableException e) {
            return ResponseEntity.status(409).build();
        }
    }
}