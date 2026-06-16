package com.university.library.service;

import com.university.library.exception.BookNotFoundException;
import com.university.library.exception.NoCopiesAvailableException;
import com.university.library.exception.ReaderNotFoundException;
import com.university.library.model.Book;
import com.university.library.model.Loan;
import com.university.library.repository.BookRepository;
import com.university.library.repository.LoanRepository;
import com.university.library.repository.ReaderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class LoanService {
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    public LoanService(BookRepository bookRepository, ReaderRepository readerRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.loanRepository = loanRepository;
    }

    public void issueLoan(UUID bookId, UUID readerId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        readerRepository.findById(readerId)
                .orElseThrow(() -> new ReaderNotFoundException(readerId));

        // TODO (W2 review): check-then-act гонка — два параллельных запроса могут оба пройти copies>0 и увести
        //  copies_available в минус. Лечится в W3: @Transactional + блокировка (та же гонка, что ловили в W1).
        // TODO (W2 review): UUID.randomUUID() ниже выбрасывается впустую — LoanRepository.save не пишет id,
        //  БД генерит свой через gen_random_uuid(). Либо вставляй этот id, либо не создавай его в Java.
        if (book.getCopiesAvailable() > 0) {
            LocalDate today =  LocalDate.now();
            Loan loan = new Loan(UUID.randomUUID(), bookId, readerId, LocalDate.now(), today.plusDays(14), false);
            loanRepository.save(loan);
            book.setCopiesAvailable(book.getCopiesAvailable() - 1);
            bookRepository.update(book);
        } else  { throw new NoCopiesAvailableException("No copies available"); }
    }
}
