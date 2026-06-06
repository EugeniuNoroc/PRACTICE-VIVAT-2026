package com.university.library.model;

import java.time.LocalDate;
import java.util.UUID;

public class Loan {
    private UUID id;
    private UUID bookId;
    private UUID readerId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;

    public Loan(UUID id, UUID bookId, UUID readerId, LocalDate loanDate, LocalDate returnDate, boolean returned) {
        this.id = id;
        this.bookId = bookId;
        this.readerId = readerId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.returned = returned;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookId() {
        return bookId;
    }

    public UUID getReaderId() {
        return readerId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returned;
    }
}
