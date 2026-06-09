package com.university.library.controller;

import com.university.library.exception.NoCopiesAvailableException;
import com.university.library.service.LoanService;
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

    @PostMapping
    public ResponseEntity<Void> issueLoan(@RequestParam UUID bookId, @RequestParam UUID readerId) {
        try {
            loanService.issueLoan(bookId, readerId);
            return ResponseEntity.status(201).build();
        } catch (NoCopiesAvailableException e) {
            return ResponseEntity.status(409).build();
        }
    }
}