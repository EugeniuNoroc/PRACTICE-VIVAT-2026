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
    // TODO (W2 review): по заданию (день 3, ч.4) тело должно быть JSON { "bookId":..., "readerId":... }
    //  через @RequestBody, а не два @RequestParam.
    @PostMapping
    public ResponseEntity<Void> issueLoan(@RequestParam UUID bookId, @RequestParam UUID readerId) {
        try {
            loanService.issueLoan(bookId, readerId);
            return ResponseEntity.status(201).build();
        } catch (NoCopiesAvailableException e) {
            // TODO (W2 review): этот локальный catch отдаёт ПУСТОЙ 409 и перехватывает управление до
            //  GlobalExceptionHandler.handleConflict, который положил бы сообщение "No copies available".
            //  Убери try/catch — пусть исключение долетит до advice (день 3 demo: "409 с понятным сообщением").
            return ResponseEntity.status(409).build();
        }
    }

    // TODO (W2 review): контроллер неполный — по заданию (день 3, ч.4) не хватает endpoints:
    //  1) POST /api/loans/{id}/return — вернуть книгу (loan.returned=true + инкремент copies_available);
    //  2) GET  /api/loans?readerId=... — все выдачи читателя.
    //  Нужны LoanService.returnLoan(id)/findByReader(readerId) и LoanRepository.findByReaderId(readerId).
}